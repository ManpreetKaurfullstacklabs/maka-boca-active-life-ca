import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useDispatch } from "react-redux";
import { setCourses } from "../../redux/OfferedCourcesSlice.js";
import 'react-toastify/dist/ReactToastify.css';
import { toast, ToastContainer } from 'react-toastify';
import {removeFromCart as reduxRemoveFromCart} from "../../redux/CartSlice.js";
import {FaTrash} from "react-icons/fa";

const EnrolledCourses = () => {
    const navigate = useNavigate();
    const dispatch = useDispatch();
    const [error, setError] = useState("");
    const [allCourses, setAllCourses] = useState([]);
    const authToken = localStorage.getItem("jwtToken");
    const familyMemberId = localStorage.getItem("familyMemberId");



    const fetchCourses = async () => {
        try {
            const res = await fetch(`http://localhost:40015/api/courseregistration/familyMemberId/${familyMemberId}`, {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${authToken}`,
                },
            });


            if (res.ok) {
                const responseData = await res.json();
                console.log(responseData)
                const filteredCourses = responseData.filter(course => course.isWithdrawn !== "YES");
                if (Array.isArray(filteredCourses)) {
                    setAllCourses(filteredCourses);
                    dispatch(setCourses(filteredCourses));
                } else {
                    setAllCourses([ filteredCourses]);
                }
            } else {
                setError("Error while loading courses.");
            }
        } catch (error) {
            console.error("Error:", error);
            setError("Error while loading courses.");
        }
    };
    ///member/

    const WithdrawCourse = async (course) => {
        try {

            const response = await fetch(`http://localhost:40015/api/courseregistration/member/${course.familyCourseRegistrationId}`, {
                method: "DELETE",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${authToken}`
                }
            });

            if (response.ok) {

                dispatch(reduxRemoveFromCart({ offeredCourseId: course.offeredCourseId }));
                setAllCourses((prevCourses) =>
                    prevCourses.filter(c => c.familyCourseRegistrationId !== course.familyCourseRegistrationId)
                );

                toast.success('Member withdrawn!', {
                    position: 'top-center',
                });
            } else {
                const result = await response.json();
                toast.warning(result.message || "Failed to Withdraw", {
                    position: 'top-right',
                });
            }
        } catch (error) {
            console.error("Error while Withdrawing:", error);
            toast.error('Error while withdrawing a course', {
                position: 'top-right',
            });
        }
    };

    useEffect(() => {
        fetchCourses();
    }, [authToken, familyMemberId ]);

    return (
        <div style={{ width: '100%' }}>
            <ToastContainer
                position="bottom-center"
                autoClose={1500}
                hideProgressBar
                closeOnClick
                pauseOnHover={false}
                draggable={false}
            />
            <h1><b>Active Learning Programs</b></h1>
            {error && <p className="error-message">{error}</p>}

            <div className="course-list">
                {allCourses.length === 0 ? (
                    <p>No courses available.</p>
                ) : (
                    allCourses.map((course, index) => {
                        const courseName = course?.offeredCourseDTO?.courseDTO?.name || "Unnamed Course";
                        const city = course?.offeredCourseDTO?.facilititesDTO?.city || "Unknown City";
                        const startDate = course?.offeredCourseDTO?.startDate;
                        const endDate = course?.offeredCourseDTO?.endDate;
                        const isAvailable = course.offeredCourseDTO?.availableForEnrollment === "YES";

                        return (
                            <div
                                className="card"
                                key={index}
                                onClick={() => navigate(`/CourseDescription/${course.offeredCourseId}`)}
                            >
                                <p><b>Course Name:</b> {courseName}</p>
                                <p><b>City:</b> {city}</p>
                                <p><b>Start:</b> {startDate}</p>
                                <p><b>End:</b> {endDate}</p>
                                <button
                                    className={`cta-button ${!isAvailable ? 'disabled' : ''}`}
                                    disabled={!isAvailable}
                                    onClick={(e) => {
                                        e.stopPropagation();
                                        if (isAvailable) {
                                            WithdrawCourse(course)}
                                        }
                                    }
                                >
                                    {isAvailable ? "Withdraw" : "Not Available"}

                                </button>
                            </div>
                        );
                    })
                )}
            </div>
        </div>
    );
};

export default EnrolledCourses;
