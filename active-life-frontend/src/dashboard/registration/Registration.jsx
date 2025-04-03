
import "./Registration.css";
import {useLocation, useNavigate} from "react-router-dom";
import {useEffect, useState} from "react";
import cardio from '../../assets/cardio.jpg'



const Registration = () => {
    const navigate = useNavigate();
    const { state: { memberLoginId, jwtToken } } = useLocation()

    const[courses, setCourses] = useState([]);
    const[member, setMember] = useState([])
    const[error, setError] = useState(" ");

    const  authToken = localStorage.getItem("jwtToken", jwtToken)
    const loginId = localStorage.getItem("memberLoginId", memberLoginId)
    console.log(loginId)

        useEffect(()=>{
            const fetchCourse = async () =>{
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
                        setCourses(responseData);
                        const memberData = await memberInfo.json();
                        setMember(memberData)
                        console.log(memberData)


                    } else {
                        setError("error while loading");
                    }
                } catch (error) {
                    console.error("Error:", error);
                    setError("Invalid credentials. Please try again.");
                }
            }

            if (authToken){
                fetchCourse()
            }
            else {
                setError("error while loading ")
            }
        }, [authToken]);

    const handleLogout = () => {
        localStorage.removeItem("jwtToken");
        localStorage.removeItem("memberLoginId");
        navigate("/login");
    };


    return (
        <div >
            <h1>Offered Courses</h1>
            <div className="item-container">
                <div className="top-nav-bar">
                    <div className="member-name">
                     Welcome  {member && member.memberName ? member.memberName : "Member Name"}
                    </div>
                    <div className="logout">
                        <button onClick={handleLogout}>Logout</button>
                    </div>
                </div>

                <div>
                    <h1>Available Courses</h1>
                    {error && <p className="error-message">{error}</p>}
                    <div className="course-list">
                        {courses.map((course) => (
                            <div className="card" key={course.coursesId}>
                                <img className="img" src={cardio} alt="Cardio" />
                                <p><b>Course Name</b> : {course.courseDTO.subcategories.categories.name}</p>
                                <p><b>No of seats </b>: {course.noOfSeats}</p>
                                <p><b>Start Date</b> : {course.startDate}</p>
                                <p><b>End Date </b>: {course.endDate}</p>
                                <p>{course.courseDTO.ageGroups.description}</p>
                                <p><b>Available for Enrollment </b>{course.availableForEnrollment}</p>
                                <button className={"cta-button"}>Add To Cart</button>
                            </div>
                        ))}
                    </div>
                </div>
            </div>
        </div>
    );
};
export default Registration;
