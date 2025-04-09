import "./Cart.css";
import { useDispatch, useSelector } from "react-redux";
import {addToCart, clearCart, loadDataFromBackend, removeFromCart as reduxRemoveFromCart} from "../redux/CartSlice";
import { toast, ToastContainer } from "react-toastify";
import React, {useEffect, useState} from "react";
import { FaTrash } from "react-icons/fa";
import {fetchCart} from "../redux/fetchCart.js";


const Cart = () => {
    const dispatch = useDispatch();
    const cartItems = useSelector((state) => state?.cart?.items || []);
    const courses = useSelector((state) => state?.offeredCourses?.courses || []);
    const authToken = localStorage.getItem("jwtToken");
    const [paymentSuccess, setPaymentSuccess] = useState(false);



    useEffect(() => {
        fetchCart(authToken).then(
            (data) => {
                dispatch(loadDataFromBackend(data.courses))
            }
        )
    }, [authToken, dispatch]);


    const courseMap = courses.reduce((acc, course) => {
        acc[course.offeredCourseId] = course;
        return acc;
    }, {});

    const proceedToBulkPayment = async () => {
        try {
            const offeredCourseIds = cartItems.map(item => item.offeredCourseId);

            const totalAmount = cartItems.reduce((total, item) => {
                const course = courseMap[item.offeredCourseId];
                return total + (course?.offeredCourseFeeDTO?.courseFee || 0);
            }, 0);

            const cartItem = {
                familyMemberId: localStorage.getItem("familyMemberId"),
                amount: totalAmount,
                paymentMethod: "CREDIT_CARD",
                offeredCourseIds
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
                toast.success('Payment succeeded!', {
                    position: 'top-center',
                });
                dispatch(clearCart());
                const familyMemberId = localStorage.getItem("familyMemberId");
                localStorage.removeItem(`cart_${familyMemberId}`);
                setPaymentSuccess(true);
                // cartItems.clear;
            } else {
            //    alert(result.message)
                toast.warning(result.message  , {
                    position: 'top-center',
                });
            }
        } catch (error) {
            console.error("Error during bulk payment:", error);
            toast.error('An error occurred during the payment process.', {
                position: 'top-center',
            });
        }
    };

    const handleRemoveFromCart = async (course) => {
        try {
            const familyMemberId = localStorage.getItem("familyMemberId");
            const response = await fetch(`http://localhost:40015/api/shoppingcart/delete/${familyMemberId}/${course.offeredCourseId}`, {
                method: "DELETE",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${authToken}`
                }
            });

            if (response.ok) {

                dispatch(reduxRemoveFromCart({ offeredCourseId: course.offeredCourseId }));

                toast.success('Removed from cart!', {
                    position: 'top-center',
                });
            } else {
                const result = await response.json();
                toast.warning(result.message || "Failed to remove course", {
                    position: 'top-right',
                });
            }
        } catch (error) {
            console.error("Error while removing from cart:", error);
            toast.error('Error while removing from cart', {
                position: 'top-right',
            });
        }
    };

    const totalPrice = cartItems.reduce((total, item) => {
        const course = courseMap[item.offeredCourseId];
        return total + (course?.offeredCourseFeeDTO?.courseFee || 0);
    }, 0);

    return (
        <div className="cart-box">
            <ToastContainer
                position="top-right"
                autoClose={1500}
                hideProgressBar
                closeOnClick
                pauseOnHover={false}
                draggable={false}
            />
            <h1>Cart Items</h1>

            {cartItems.length === 0 ? (
                <p>No items in cart.</p>
            ) : (
                <>
                    <div className="cart-header">
                        <div>Course ID</div>
                        <div>Name</div>
                        <div>Price</div>
                        <div>Action</div>
                    </div>
                    {cartItems.map((item) => {
                        const course = courseMap[item.offeredCourseId];
                        if (!course) return null;
                        return (
                            <div className="cart-item" key={item.offeredCourseId}>
                                <p>{course.barcode}</p>
                                <p>{course.courseDTO.subcategories.name}</p>
                                <p>${course.offeredCourseFeeDTO.courseFee}</p>
                                <button
                                    className="cart-button"
                                    onClick={() => handleRemoveFromCart(course)}
                                >
                                    <FaTrash />
                                </button>
                            </div>
                        );
                    })}

                    <p className="total-price"><b>Total:</b> ${totalPrice.toFixed(2)}</p>

                    <button className="cta-button" onClick={proceedToBulkPayment}>
                        Proceed To Payment
                    </button>
                </>
            )}

        </div>
    );
};

export default Cart;
