import "./Registration.css";
import {useLocation, useNavigate} from "react-router-dom";
import React, {useEffect, useState} from "react";
import {addToCart, loadDataFromBackend} from "../../redux/CartSlice.js";
import {useDispatch, useSelector} from "react-redux";
import {setCourses} from "../../redux/OfferedCourcesSlice.js";
import 'react-toastify/dist/ReactToastify.css';
import { toast, ToastContainer, Slide } from 'react-toastify';
import {fetchCart} from "../../redux/fetchCart.js";
import {store} from "../../redux/store.js";

const Registration = () => {
    const navigate = useNavigate();
    const {state: {memberLoginId, jwtToken}} = useLocation()
    const dispatch = useDispatch();

    const courses = useSelector((state) => state.offeredCourses.courses);
    const [member, setMember] = useState([])
    const [error, setError] = useState(" ");
    const [searchQuery, setSearchQuery] = useState("");

    const authToken = localStorage.getItem("jwtToken")
    const loginId = localStorage.getItem("memberLoginId" )
    const familyMemberId = localStorage.getItem("familyMemberId")
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
                    fetchCart(authToken).then(
                        (data) => {
                            store.dispatch(loadDataFromBackend(data.courses))
                        }
                    )
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

    const handleAddToCart = async (course) => {
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

    const handleSearchChange = (e) => {
        setSearchQuery(e.target.value);
    };

    const filteredCourses = courses.filter(course => {
        // Safely access subcategories with optional chaining
        const subcategories = course?.courseDTO?.subcategories;
        if (subcategories && subcategories.name) {
            return subcategories.name.toLowerCase().includes(searchQuery.toLowerCase());
        }
        return false;
    });

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
            <h1>Available Courses</h1>
            <div>
                <input
                    type="text"
                    placeholder="Search courses..."
                    value={searchQuery}
                    onChange={handleSearchChange}
                    className="search-bar"
                />
            </div>
            <div className="course-list">
                {filteredCourses.length === 0 ? (
                    <p>No courses found matching your search.</p>
                ) : (
                    filteredCourses.map((course) => {
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
                            const isAvailable = course.availableForEnrollment === "YES";
                            return (
                                <div className="card" key={course.coursesId}
                                     onClick={() => {
                                         navigate(`/CourseDescription/${course.offeredCourseId}`);
                                     }}>
                                    <img className="img" src={courseImage} alt={courseName}/>
                                    <p><b>{course.barcode}</b></p>
                                    <p><b>Course Name</b>: {courseName}</p>
                                    <p><b>No of seats </b>: {course.noOfSeats}</p>
                                    <p><b>Start Date</b>: {course.startDate}</p>
                                    <p><b>End Date </b>: {course.endDate}</p>
                                    <p>{course.courseDTO.ageGroups.description}</p>
                                    <p><b>Available for Enrollment</b> {course.availableForEnrollment}</p>
                                    <p><b> Price :</b>${course.offeredCourseFeeDTO.courseFee}</p>
                                    <button
                                        className={`cta-button ${!isAvailable ? 'disabled' : ''}`}
                                        disabled={!isAvailable}
                                        onClick={(e) => {
                                            e.stopPropagation(); handleAddToCart(course)
                                        }}>{isAvailable ? "Add To cart" : "Not Available"}
                                    </button>
                                </div>
                            );
                        }
                    )
                )}
            </div>
        </div>
    );
};
export default Registration;
