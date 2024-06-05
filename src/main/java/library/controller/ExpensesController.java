package library.controller;

import io.javalin.http.Handler;
import library.model.Expense;
import library.model.Person;
import library.persistence.ExpenseDAO;
import library.server.Routes;
import library.server.ServiceRegistry;
import library.server.WeShareServer;

import javax.money.MonetaryAmount;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import static library.model.MoneyHelper.amountOf;

;
public class ExpensesController {
        public static final Handler view = context -> {
            ExpenseDAO expensesDAO = ServiceRegistry.lookup(ExpenseDAO.class);
            Person personLoggedIn = WeShareServer.getPersonLoggedIn(context);

            Collection<Expense> allExpenses = expensesDAO.findExpensesForPerson(personLoggedIn);

            ArrayList<Expense> expenses = new ArrayList<>();
            for (Expense expense : allExpenses) {
                if (!expense.isFullyPaidByOthers()) {
                    expenses.add(expense);
                }
            }

            MonetaryAmount totalAmount = amountOf(0);
            for (Expense request:expenses) {
                System.out.println(request.getDescription());
                totalAmount = totalAmount.add(request.getAmount());
            }
            System.out.print(expenses);

            Map<String, Object> viewModel = Map.of("expenses", expenses,"totalAmount",totalAmount);
            context.render("expenses.html", viewModel);

        };
    public static final Handler addExp = context -> {
        ExpenseDAO expensesDAO = ServiceRegistry.lookup(ExpenseDAO.class);
        Person personLoggedIn = WeShareServer.getPersonLoggedIn(context);

        Collection<Expense> expenses = expensesDAO.findExpensesForPerson(personLoggedIn);

        Map<String, Object> viewModel = Map.of("newexpense", expenses);
        context.render("addNewExpense.html", viewModel);

    };

    public static final Handler submitForm = context -> {
        ExpenseDAO expensesDAO = ServiceRegistry.lookup(ExpenseDAO.class);
        Person personLoggedIn = WeShareServer.getPersonLoggedIn(context);

        String description = context.formParamAsClass("description", String.class).get();
        String amount = context.formParamAsClass("amount", String.class).get();
        String date = context.formParamAsClass("date", String.class).get();

        LocalDate expensedate = LocalDate.parse(date);
        Expense expenseToSave = new Expense(personLoggedIn,description,amountOf(Integer.parseInt(amount)),expensedate);

        expensesDAO.save(expenseToSave);

        context.redirect(Routes.EXPENSES);
    };

    public static final Handler requestForm = context -> {
        ExpenseDAO expensesDAO = ServiceRegistry.lookup(ExpenseDAO.class);
        Person personLoggedIn = WeShareServer.getPersonLoggedIn(context);

        String id = context.queryParam("expenseId");
        Collection<Expense> expenses = expensesDAO.findExpensesForPerson(personLoggedIn);

        Expense expenseclick = null;
        for (Expense expense : expenses) {
            if (expense.getId().toString().equals(id)) {
                expenseclick = expense;
            }
        }

        Map<String, Object> viewModel = Map.of("expense", expenseclick);
        context.render("paymentRequest.html", viewModel);
    };

    public static final Handler expenseForm = context -> {
        ExpenseDAO expensesDAO = ServiceRegistry.lookup(ExpenseDAO.class);
        Person personLoggedIn = WeShareServer.getPersonLoggedIn(context);

        String id = context.queryParam("expenseId");
        Collection<Expense> expenses = expensesDAO.findExpensesForPerson(personLoggedIn);
        Expense expensepost = null;
        for (Expense expense : expenses) {
            if (expense.getId().toString().equals(id)) {
                expensepost = expense;
                break;

            }
        }

        Map<String, Object> viewModel = Map.of("expense", expensepost);
//        System.out.println(expensepost.listOfPaymentRequests());
        context.render("paymentRequest.html", viewModel);
    };}





