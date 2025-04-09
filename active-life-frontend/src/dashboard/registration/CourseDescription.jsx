
import "./CourseDescription.css";
import {useLocation, useNavigate, useParams } from "react-router-dom";
import React, {useEffect, useState} from "react";
import {addToCart} from "../../redux/CartSlice.js";
import {useDispatch} from "react-redux";
import 'react-toastify/dist/ReactToastify.css';
import { toast, ToastContainer, Slide } from 'react-toastify';





const CourseDescription = () => {
    const navigate = useNavigate();
    const params = useParams()
    const[course, setCourse] = useState({})
    const[loading, setLoading] = useState(true)
    const dispatch = useDispatch();

    const[error, setError] = useState(" ");


    const authToken = localStorage.getItem("jwtToken")
    const loginId = localStorage.getItem("memberLoginId")
    const familyMemberId = localStorage.getItem("familyMemberId")
    console.log(authToken, loginId)
    console.log(params);
    const { id } = useParams();



    useEffect(()=>{
        const fetchCourse = async () =>{
            try {
                const res = await fetch(`http://localhost:40015/api/offeredcourse/${id}`, {
                    method: "GET",
                    headers: {
                        "Content-Type": "application/json",
                        "Authorization": `Bearer ${authToken}`
                    }
                })
                if (res.ok) {
                    const responseData = await res.json();
                    setCourse(responseData);
                    setLoading(false)

                } else {
                    setError("error while loading");
                }
            } catch (error) {
                console.error("Error:", error);
                setLoading(false)
                setError("Invalid credentials. Please try again.");
            }
        }

        if (authToken && id){
            fetchCourse()
        }
        else {
            setError("error while loading ")
        }
    }, [authToken, id]);


    const getImageForCourse = (name) => {
        const imageKey = name?.split(" ")[0];
        switch (imageKey) {
            case "Hatha":
                return "/Hatha-yoga.jpg";
            case "Mindful":
                return "/Mindful-Meditation.jpg";
            case "Lose":
                return "/Weight-Lose.jpg";
            case "Healthy":
                return "/Healthy-meals.jpg";
            default:
                return "/Cardio.jpg";
        }
    };

    if (loading) {
        return <h6> Loading ...</h6>
    }

    if (Object.keys(course).length === 0) {
        return <h6> Sorry do not have ...</h6>
    }

    const   handleAddToCart = async (course) => {
        try {
            const cartItem = {
                familyMemberId: localStorage.getItem("familyMemberId"),
                offeredCourseId: course.offeredCourseId,
                quantity: 1,
            };
            const response = await fetch("http://localhost:40015/api/shoppingcart/add-to-cart", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${authToken}`
                },
                body: JSON.stringify(cartItem)
            });

            if (response.ok) {
                dispatch(addToCart({ ...course, familyMemberId }));

                console.log("Success toast should show");
                //   alert("added to cart")
                toast.success('Course added to cart successfully!', {
                    position: 'top-right',
                });

            }
            else {
                if (response.status === 409) {
                    toast.warning('Course is already in the cart!', {
                        position:'top-right',
                    });
                }
            }
        } catch (error) {
            console.error("Error while adding to cart:", error);
            toast.error(' Error while adding to cart:', {
                position: 'top-right',
            });
        }
    };

    return (
        <div>
            <ToastContainer
                position="bottom-center"
                autoClose={1500}
                hideProgressBar
                closeOnClick
                pauseOnHover={false}
                draggable={false}
            />
            <h1 className="course-title">Course Details</h1>

            <div className="course-container">
                <img
                    className="imgCourse-container"
                    src={getImageForCourse(course?.courseDTO?.subcategories?.name)}
                    alt={course?.courseDTO?.subcategories?.name}
                />
                <div className="course-details">
                    {error && <p className="error-message">{error}</p>}
                    <p><b>{course.barcode}</b></p>
                    <p><b>Course Name:</b> {course.courseDTO.name}</p>
                    <p><b>No of seats:</b> {course.noOfSeats}</p>
                    <p><b>Start Date:</b> {course.startDate}</p>
                    <p><b>End Date:</b> {course.endDate}</p>
                    <p>{course.courseDTO.ageGroups.description}</p>
                    <p><b>Available for Enrollment:</b> {course.availableForEnrollment}</p>
                    <p><b>Price:</b> ${course.offeredCourseFeeDTO.courseFee}</p>

                    <button className="cta-button" onClick={(e) => {
                        e.stopPropagation(); handleAddToCart(course)
                    }}>Add To Cart
                    </button>
                </div>
            </div>
            <h3 className={"course-container"}>  Description</h3>
            <div className={"course-container"}>
            <p>
                <b>{course.courseDTO.name}</b> is a <b>{course.courseDTO.subcategories.name}</b> course under the <b>{course.courseDTO.subcategories.categories.name}</b> category.
                This program focuses on <i>{course.courseDTO.description}</i>. It is suitable for individuals in the <b>{course.courseDTO.ageGroups.description}</b> age group.
                The course starts on <b>{course.startDate }</b> and ends on <b>{course.endDate }</b>. Classes begin at <b>{course.startTime   }</b>   and conclude at <b>{course.endTime}</b>.
                This is {course.isAllDay === "YES" ? "an all-day program" : "a scheduled session-based course"}. Enrollment starts from <b>{course.registrationStartDate}</b>
                The enrollment status is currently <b>{course.availableForEnrollment === "YES" ? "Open" : "Closed"}</b>.
                The fee for this course is <b>${course.offeredCourseFeeDTO.courseFee}</b> for <b>{course.offeredCourseFeeDTO.feeType.toLowerCase()} students</b>.
            </p>
            </div>
        </div>

    );


};
export default CourseDescription;
