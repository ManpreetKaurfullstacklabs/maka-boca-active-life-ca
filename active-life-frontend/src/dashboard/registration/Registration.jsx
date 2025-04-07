import "./Registration.css";
import {useLocation, useNavigate} from "react-router-dom";
import {useEffect, useState} from "react";
import {addToCart} from "../../redux/CartSlice.js";
import {useDispatch, useSelector} from "react-redux";
import {setCourses} from "../../redux/OfferedCourcesSlice.js";




const Registration = () => {
    const navigate = useNavigate();
    const {state: {memberLoginId, jwtToken}} = useLocation()
    const dispatch = useDispatch();

 //   const [courses, setCourses] = useState([]);

    const courses = useSelector((state) => state.offeredCourses.courses);
    const [member, setMember] = useState([])
    const [error, setError] = useState(" ");

    const authToken = localStorage.getItem("jwtToken", jwtToken)
    const loginId = localStorage.getItem("memberLoginId", memberLoginId)
    console.log(loginId)


    useEffect(() => {
        const fetchCourse = async () => {
            try {
                const res = await fetch("http://localhost:40015/api/offeredcourse", {
                    method: "GET",
                    headers: {
                        "Content-Type": "application/json",
                        "Authorization": `Bearer ${authToken}`
                    }
                })
                const memberInfo = await fetch("http://localhost:40015/api/familyregistration/" + loginId, {
                        method: "GET",
                        headers: {
                            "Content-Type": "application/json",
                            "Authorization": `Bearer ${authToken}`
                        }
                    }
                );
                if (res.ok) {
                    const responseData = await res.json();
                    dispatch(setCourses(responseData));
                    const memberData = await memberInfo.json();
                    setMember(memberData)
                    localStorage.setItem("familyMemberId", memberData.familyMemberId)
                }
                 else {
                    setError("error while loading");
                }
            } catch (error) {
                console.error("Error:", error);
                setError("Invalid credentials. Please try again.");
            }
        }

        if (authToken) {
            fetchCourse()
        } else {
            setError("error while loading ")
        }
    }, [authToken]);

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
                dispatch(addToCart(cartItem));
                alert("added to cart")
            }
            else {
                if (response.status === 409) {
                    alert('Course is already in the cart!');
                }
            }
        } catch (error) {
            console.error("Error while adding to cart:", error);
        }
    };

    return (
        <div>
            <h1>Available Courses</h1>
            {error && <p className="error-message">{error}</p>}
            <div className="course-list">
                {courses.map((course) => {
                    const courseName = course.courseDTO.subcategories.name;
                    const imageName = courseName.split(" ")[0];
                    let courseImage;
                    switch (imageName) {
                        case "Hatha":
                            courseImage = "/public/Hatha-yoga.jpg";
                            break;
                        case "Mindful":
                            courseImage = "/public/Mindful-Meditation.jpg";
                            break;
                        case "Lose":
                            courseImage = "/public/Weight-Lose.jpg";
                            break;
                        case "Healthy":
                            courseImage = "/public/Healthy-meals.jpg";
                            break;
                        default:
                            courseImage = "public/Cardio.jpg";
                    }
                    return (
                        <div className="card" key={course.coursesId}
                             onClick={() => {
                                 navigate(`/CourseDescription/${course.offeredCourseId}`);
                             }}
                        >
                            <img className="img" src={courseImage} alt={courseName}/>
                            <p><b>{course.barcode}</b></p>
                            <p><b>Course Name</b>: {courseName}</p>
                            <p><b>No of seats </b>: {course.noOfSeats}</p>
                            <p><b>Start Date</b>: {course.startDate}</p>
                            <p><b>End Date </b>: {course.endDate}</p>
                            <p>{course.courseDTO.ageGroups.description}</p>
                            <p><b>Available for Enrollment</b> {course.availableForEnrollment}</p>
                            <p><b> Price :</b>${course.offeredCourseFeeDTO.courseFee}</p>
                            <button className="cta-button" onClick={(e) => {
                                e.stopPropagation(); handleAddToCart(course)
                            }}>Add To Cart
                            </button>
                        </div>
                    );
                })}
            </div>
        </div>
    );
};
export default Registration;
