package com.sitare.service;
import com.razorpay.PaymentLink;
import com.razorpay.RazorpayException;
import com.stripe.exception.StripeException;
import com.sitare.domain.PaymentMethod;
import com.sitare.exception.UserException;
import com.sitare.modal.PaymentOrder;
import com.sitare.payload.dto.BookingDTO;
import com.sitare.payload.dto.UserDTO;
import com.sitare.payload.response.PaymentLinkResponse;

public interface PaymentService {

    PaymentLinkResponse createOrder(UserDTO user,
                                    BookingDTO booking, PaymentMethod paymentMethod) throws RazorpayException, UserException, StripeException;

    PaymentOrder getPaymentOrderById(Long id) throws Exception;

    PaymentOrder getPaymentOrderByPaymentId(String paymentId) throws Exception;

    Boolean ProceedPaymentOrder (PaymentOrder paymentOrder,
                                 String paymentId, String paymentLinkId) throws RazorpayException;

    PaymentLink createRazorpayPaymentLink(UserDTO user,
                                          Long Amount,
                                          Long orderId) throws RazorpayException;

    String createStripePaymentLink(UserDTO user, Long Amount,
                                            Long orderId) throws StripeException;
}
