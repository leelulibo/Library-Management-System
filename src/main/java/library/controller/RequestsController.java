package library.controller;

import io.javalin.http.Handler;
import library.model.Expense;
import library.model.PaymentRequest;
import library.model.Person;
import library.persistence.ExpenseDAO;
import library.server.Routes;
import library.server.ServiceRegistry;
import library.server.WeShareServer;

import javax.money.MonetaryAmount;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import static library.model.MoneyHelper.amountOf;


public class RequestsController {


    public static final Handler view = context -> {

        ExpenseDAO expensesDAO = ServiceRegistry.lookup(ExpenseDAO.class);
        Person personLoggedIn = WeShareServer.getPersonLoggedIn(context);
        Collection<PaymentRequest> requests = expensesDAO.findPaymentRequestsSent(personLoggedIn);
        MonetaryAmount totalAmountRequests = amountOf(0);
        for (PaymentRequest request:requests) {
            totalAmountRequests = totalAmountRequests.add(request.getAmountToPay()) ;
            System.out.println(totalAmountRequests+"kkkk");
        }
        Map<String, Object> viewModel = Map.of("requests", requests, "totalAmountRequests",totalAmountRequests);
        context.render("paymentrequests_sent.html", viewModel);

    };

    public static final Handler received = context -> {

        ExpenseDAO expensesDAO = ServiceRegistry.lookup(ExpenseDAO.class);
        Person personLoggedIn = WeShareServer.getPersonLoggedIn(context);

        Collection<PaymentRequest> requests = expensesDAO.findPaymentRequestsReceived(personLoggedIn);
        MonetaryAmount total = amountOf(0);

        for (PaymentRequest request:requests) {
            System.out.println(request.getAmountToPay());
            total = total.add(request.getAmountToPay()) ;
        }
        System.out.println(total+"kkkk");

        Map<String, Object> viewModel = Map.of("requests", requests, "total",total);
        context.render("paymentRequestsReceived.html", viewModel);

    };

    public static final Handler payRequest = context -> {
        ExpenseDAO expensesDAO = ServiceRegistry.lookup(ExpenseDAO.class);
        Person personLoggedIn = WeShareServer.getPersonLoggedIn(context);

        String id = context.formParamAsClass("id", String.class).get();
        Collection<PaymentRequest> requests = expensesDAO.findPaymentRequestsReceived(personLoggedIn);
        MonetaryAmount total = amountOf(0);
        PaymentRequest payment= null;
        Expense expense = null;
        for (PaymentRequest req : requests) {
            if(Objects.equals(req.getId().toString(), id)){
                payment= req;
                expense = req.getExpense();
                total = total.add(req.getAmountToPay());
            }

        }
        expensesDAO.save(new Expense(personLoggedIn, expense.getDescription(), total,expense.getDate()));
        payment.pay(payment.getPersonWhoShouldPayBack(),LocalDate.now());
        context.redirect(Routes.REQUESTS_RECEIVED);
    };
    public static final Handler requestPayment = context -> {

        ExpenseDAO expensesDAO = ServiceRegistry.lookup(ExpenseDAO.class);
        Person personLoggedIn = WeShareServer.getPersonLoggedIn(context);
        String email = context.formParam("email");
        int amount = context.formParamAsClass("amount", Integer.class).get();
        String date = context.formParamAsClass("due_date", String.class).get();
        String id = context.formParamAsClass("expenseId", String.class).get();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate converted_date = LocalDate.parse(date, format);

        Collection<Expense> expenses = expensesDAO.findExpensesForPerson(personLoggedIn);
        Expense expensepost = null;
        for (Expense expense : expenses) {
            if (expense.getId().toString().equals(id)) {
                expensepost = expense;
                break;

            }
        }
        PaymentRequest request = new PaymentRequest(expensepost, new Person(email), amountOf(amount), converted_date);

        expensepost.requestPayment(request.getPersonWhoShouldPayBack(), amountOf(amount), converted_date);
//        Map<String, Object> newModel = new HashMap<>();
//        newModel.put("request", request);
//        context.render("paymentRequest.html", newModel);
        String path = Routes.PAYMENTREQUEST + "?expenseId=" + id;
        context.redirect(path);

    };

}
