package library.server;

import library.controller.ExpensesController;
import library.controller.PersonController;
import library.controller.RequestsController;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.post;

public class Routes {
    public static final String LOGIN_PAGE = "/";
    public static final String LOGIN_ACTION = "/login.action";
    public static final String LOGOUT = "/logout";
    public static final String EXPENSES = "/expenses";
    public static final String REQUESTS = "/paymentrequests_sent";
    public static final String REQUESTS_RECEIVED = "/paymentrequests_received";
    public static final String PAYMENT_ACTION = "/payment.action";
    public static final String ADDNEWEXPENSE = "/newexpense";
    public static final String SUBMITEXPENSE = "/expense.action";
    public static final String PAYMENTREQUEST = "/paymentrequest";
    public static final String PAYMENT = "/payment";
    public static final String PAYMENTREQUESTACTION = "/paymentrequest.action";


    public static void configure(WeShareServer server) {
        server.routes(() -> {
            post(LOGIN_ACTION,  PersonController.login);
            get(LOGOUT,         PersonController.logout);
            get(EXPENSES,           ExpensesController.view);
            get(REQUESTS, RequestsController.view);
            get(REQUESTS_RECEIVED, RequestsController.received);
            post(PAYMENT_ACTION, RequestsController.payRequest);
            get(ADDNEWEXPENSE, ExpensesController.addExp);
            post(SUBMITEXPENSE, ExpensesController.submitForm);
            get(PAYMENTREQUEST, ExpensesController.requestForm);
            post(PAYMENT, ExpensesController.expenseForm);
            post(PAYMENTREQUESTACTION, RequestsController.requestPayment);







        });
    }
}
