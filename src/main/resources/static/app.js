const BASE_URL = "http://localhost:8080/api/v1";

function print(data) {
    document.getElementById("output").innerText =
        JSON.stringify(data, null, 2);
}

// ==================== SIGNUP ====================

async function signup() {

    const body = {
        name: document.getElementById("signupName").value,
        email: document.getElementById("signupEmail").value,
        password: document.getElementById("signupPassword").value
    };

    try {

        const res = await fetch(`${BASE_URL}/auth/signup`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(body)
        });

        if (!res.ok) {
            throw new Error(await res.text());
        }

        const data = await res.json();

        print(data);

        alert("Signup Successful");

    } catch (e) {
        console.error(e);
        alert(e.message);
    }

}

// ==================== LOGIN ====================

async function login() {

    const body = {
        email: document.getElementById("loginEmail").value,
        password: document.getElementById("loginPassword").value
    };

    try {

        const res = await fetch(`${BASE_URL}/auth/login`, {
            method: "POST",
            credentials: "include",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(body)
        });

        if (!res.ok) {
            throw new Error("Login Failed");
        }

        alert("Login Successful");

    } catch (e) {
        console.error(e);
        alert(e.message);
    }
}

// ==================== REFRESH ====================

async function refreshToken() {

    try {

        const res = await fetch(`${BASE_URL}/auth/refresh`, {
            method: "POST",
            credentials: "include"
        });

        if (!res.ok) {
            throw new Error("Refresh Failed");
        }

        alert("Access Token Refreshed");

    } catch (e) {
        console.error(e);
        alert(e.message);
    }

}

// ==================== PAYMENT ====================

async function pay() {

    const bookingId = document.getElementById("bookingId").value;

    try {

        const res = await fetch(
            `${BASE_URL}/bookings/${bookingId}/payments`,
            {
                method: "POST",
                credentials: "include"
            }
        );

        if (!res.ok) {
            throw new Error(await res.text());
        }

        const response = await res.json();

        print(response);

        console.log("Full Response:", response);
        console.log("Order Data:", response.data);

        checkout(response.data);

    } catch (e) {
        console.error(e);
        alert(e.message);
    }

}

// ==================== RAZORPAY CHECKOUT ====================

function checkout(order) {

    console.log("Order From Backend", order);

    const options = {

        // change these field names if your DTO is different
        key: order.key,

        amount: order.amount,

        currency: order.currency,

        order_id: order.orderId,

        name: "Airbnb",

        description: "Booking Payment",

        handler: async function (response) {

            console.log(response);

            await verifyPayment(response);

        },

        prefill: {
            name: "",
            email: "",
            contact: ""
        },

        theme: {
            color: "#3399cc"
        }

    };


    console.log("Order:", order);

    console.log("Key =", order.key);
    console.log("OrderId =", order.orderId);
    console.log("Amount =", order.amount);
    console.log("Currency =", order.currency);

    const razorpay = new Razorpay(options);

    razorpay.open();

}

// ==================== VERIFY PAYMENT ====================

async function verifyPayment(response) {

    const body = {

        razorpayOrderId: response.razorpay_order_id,

        razorpayPaymentId: response.razorpay_payment_id,

        razorpaySignature: response.razorpay_signature

    };

    try {

        const res = await fetch(
            `${BASE_URL}/payments/verify`,
            {
                method: "POST",
                credentials: "include",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(body)
            }
        );

        if (!res.ok) {
            throw new Error(await res.text());
        }

        const booking = await res.json();

        print(booking);

        alert("Payment Verified Successfully");

    } catch (e) {

        console.error(e);

        alert(e.message);

    }

}

// ==================== REFUND ====================

async function refund() {

    const bookingId =
        document.getElementById("refundBookingId").value;

    try {

        const res = await fetch(
            `${BASE_URL}/bookings/${bookingId}/cancel`,
            {
                method: "POST",
                credentials: "include"
            }
        );

        if (res.status === 204) {

            alert("Refund Initiated Successfully");

        } else {

            alert("Refund Failed");

        }

    } catch (e) {

        console.error(e);

        alert(e.message);

    }

}