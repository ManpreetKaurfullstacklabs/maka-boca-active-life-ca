import "./Cart.css";
import { useDispatch, useSelector } from "react-redux";
import { removeFromCart } from "../redux/CartSlice.js";

const Cart = () => {
    const dispatch = useDispatch();
    const cartItems = useSelector((state) => state?.cart?.items || []);
    const courses = useSelector((state) => state?.offeredCourses?.courses || []);
    const authToken = localStorage.getItem("jwtToken");

    const proceedToBulkPayment = async () => {
        try {
            for (const item of cartItems) {
                const course = courses.find(c => c.offeredCourseId === item.offeredCourseId);
                if (!course) continue;

                const cartItem = {
                    familyMemberId: localStorage.getItem("familyMemberId"),
                    amount: course.offeredCourseFeeDTO.courseFee,
                    paymentMethod: "CREDIT_CARD",
                };

                const response = await fetch("http://localhost:40015/api/shoppingcart/process", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                        "Authorization": `Bearer ${authToken}`
                    },
                    body: JSON.stringify(cartItem)
                });
                const result = await response.json();

                if (response.ok && result.status === "SUCCESS") {
                    console.log(` Payment succeeded for: ${course.barcode}`);
                    alert(result.message);
                } else {
                    console.warn(` Payment failed for: ${course.barcode}`);
                    alert(result.message);
                }
            }

        } catch (error) {
            console.error("Error during bulk payment:", error);
        }
    };

    const totalPrice = cartItems.reduce((total, item) => {
        const course = courses.find(course => course.offeredCourseId === item.offeredCourseId);
        return total + (course?.offeredCourseFeeDTO?.courseFee || 0);
    }, 0);

    return (
        <div className="cart-box">
            <h1>Cart Items</h1>

            {cartItems.length === 0 ? (
                <p>No items in cart.</p>
            ) : (
                <>
                    {cartItems.map((item) => {
                        const course = courses.find(course => course.offeredCourseId === item.offeredCourseId);
                        if (!course) return null;

                        return (
                            <div className="cart-item" key={item.offeredCourseId}>
                                <div className="cart-details">
                                    <p><b>Course ID:</b> {course.barcode}</p>
                                    <p><b>Name:</b> {course.courseDTO.subcategories.name}</p>
                                    <p><b>Price:</b> ${course.offeredCourseFeeDTO.courseFee}</p>
                                </div>
                                <div className="cart-actions">
                                    <button className="cart-button"
                                            onClick={() => dispatch(removeFromCart(item.offeredCourseId))}>
                                        Remove
                                    </button>
                                </div>
                            </div>
                        );
                    })}

                    <p className="total-price"><b>Total:</b> ${totalPrice.toFixed(2)}</p>

                    <button className="cta-button"
                            onClick={proceedToBulkPayment}>
                        Proceed To Payment
                    </button>
                </>
            )}
        </div>
    );
};

export default Cart;
