import "./registration/Registration.css";
import {useLocation, useNavigate} from "react-router-dom";
import {useEffect, useState} from "react";
import {useSelector} from "react-redux";



const Cart = () => {
    // const navigate = useNavigate();
    // const {state: {memberLoginId, jwtToken}} = useLocation()
    //
    // const [courses, setCourses] = useState([]);
    // const [member, setMember] = useState([])
    // const [error, setError] = useState(" ");
    //
    // const authToken = localStorage.getItem("jwtToken", jwtToken)
    // const loginId = localStorage.getItem("memberLoginId", memberLoginId)
    // console.log(loginId)
    //
    //
    // useEffect(() => {
    //     const fetchCourse = async () => {
    //         try {
    //             const res = await fetch("http://localhost:40015/api/offeredcourse", {
    //                 method: "GET",
    //                 headers: {
    //                     "Content-Type": "application/json",
    //                     "Authorization": `Bearer ${authToken}`
    //                 }
    //             })
    //             if (res.ok) {
    //                 const responseData = await res.json();
    //                 setCourses(responseData);
    //
    //
    //             } else {
    //                 setError("error while loading");
    //             }
    //         } catch (error) {
    //             console.error("Error:", error);
    //             setError("Invalid credentials. Please try again.");
    //         }
    //     }
    //
    //     if (authToken) {
    //         fetchCourse()
    //     } else {
    //         setError("error while loading ")
    //     }
    // }, [authToken]);

    const cartItems = useSelector(state => state?.cart?.items)



    return (
        <div>
            <h1> this is your cart</h1>
            {JSON.stringify(cartItems, null, 2)}
            {/*<h1>Available Courses</h1>*/}
            {/*{error && <p className="error-message">{error}</p>}*/}
            {/*<div className="course-list">*/}
            {/*    {courses.map((course) => {*/}
            {/*        const courseName = course.courseDTO.subcategories.name;*/}
            {/*        const imageName = courseName.split(" ")[0];*/}
            {/*        let courseImage;*/}
            {/*        switch (imageName) {*/}
            {/*            case "Hatha":*/}
            {/*                courseImage = "/public/Hatha-yoga.jpg";*/}
            {/*                break;*/}
            {/*            case "Mindful":*/}
            {/*                courseImage = "/public/Mindful-Meditation.jpg";*/}
            {/*                break;*/}
            {/*            case "Lose":*/}
            {/*                courseImage = "/public/Weight-Lose.jpg";*/}
            {/*                break;*/}
            {/*            case "Healthy":*/}
            {/*                courseImage = "/public/Healthy-meals.jpg";*/}
            {/*                break;*/}
            {/*            default:*/}
            {/*                courseImage = "public/Cardio.jpg";*/}
            {/*        }*/}
            {/*        return (*/}
            {/*            <div className="card" key={course.coursesId}*/}
            {/*                 onClick={() => {*/}
            {/*                     navigate(`/CourseDescription/${course.offeredCourseId}`);*/}
            {/*                 }}>*/}
            {/*                <img className="img" src={courseImage} alt={courseName}/>*/}
            {/*                <p><b>{course.barcode}</b></p>*/}
            {/*                <p><b>Course Name</b>: {courseName}</p>*/}
            {/*                <p><b>No of seats </b>: {course.noOfSeats}</p>*/}
            {/*                <p><b>Start Date</b>: {course.startDate}</p>*/}
            {/*                <p><b>End Date </b>: {course.endDate}</p>*/}
            {/*                <p>{course.courseDTO.ageGroups.description}</p>*/}
            {/*                <p><b>Available for Enrollment</b> {course.availableForEnrollment}</p>*/}
            {/*                <p><b> Price :</b>${course.offeredCourseFeeDTO.courseFee}</p>*/}
            {/*                <button className="cta-button" onClick={(e) => {*/}
            {/*                    e.stopPropagation()*/}
            {/*                }}>Add To Cart*/}
            {/*                </button>*/}
            {/*            </div>*/}
            {/*        );*/}
            {/*    })}*/}
            {/*</div>*/}
        </div>
    );
};
export default Cart;
