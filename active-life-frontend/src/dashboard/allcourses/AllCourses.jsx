import "../registration/Registration.css";
import { useNavigate } from "react-router-dom";
import React, { useEffect, useState } from "react";

import { useDispatch } from "react-redux";
import { setCourses } from "../../redux/OfferedCourcesSlice.js";
import 'react-toastify/dist/ReactToastify.css';
import { toast, ToastContainer, Slide } from 'react-toastify';

const Allcourses = () => {
    const navigate = useNavigate();
    const dispatch = useDispatch();
    const [error, setError] = useState("");
    const [searchQuery, setSearchQuery] = useState("");
    const [cityQuery, setCityQuery] = useState("");
    const [allCourses, setAllCourses] = useState([]);

    const cityList = [
        "Los Angeles",
        "San Francisco",
        "Miami",
        "Denver",
        "Chicago",
        "Dallas",
        "Houston",
        "Phoenix",
        "Las Vegas",
    ];

    useEffect(() => {
        const fetchCourses = async () => {
            try {
                const res = await fetch("http://localhost:40015/api/dashboard/search", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify({ courseName: '' })
                });
                if (res.ok) {
                    const responseData = await res.json();
                    setAllCourses(responseData);
                    dispatch(setCourses(responseData));

                } else {
                    setError("Error while loading courses.");
                }
            } catch (error) {
                console.error("Error:", error);
                setError("Error while loading courses.");
            }
        };

        fetchCourses();
    }, [dispatch]);


    const filterCoursesByName = (query) => {
        if (!query) return allCourses;
        return allCourses.filter(course =>
            course.courseDTO.subcategories.name.toLowerCase().includes(query.toLowerCase())
        );
    };

    const filterCoursesByCity = (query) => {
        if (!query) return allCourses;
        return allCourses.filter(course =>
            course.facilititesDTO.city.toLowerCase().includes(query.toLowerCase())
        );
    };

    const onChangeSearchTerm = (e) => {
        setSearchQuery(e.target.value);
    };

    const onChangeCityTerm = (e) => {
        setCityQuery(e.target.value);
    };


    const getFilteredCourses = () => {
        let filteredCourses = allCourses;

        if (searchQuery) {
            filteredCourses = filterCoursesByName(searchQuery);
        }
        if (cityQuery) {
            filteredCourses = filterCoursesByCity(cityQuery);
        }

        return filteredCourses;
    };

    return (
        <div className="item-container">
            <div style={{ width: '100%' }}>
                <ToastContainer
                    position="bottom-center"
                    autoClose={1500}
                    hideProgressBar
                    closeOnClick
                    pauseOnHover={false}
                    draggable={false}
                />
                <h1><b>Find the Perfect Course for You</b></h1>

                {error && <p className="error-message">{error}</p>}
                <div className="search-bar">
                    <div>
                        <input
                            className="search-input"
                            type="text"
                            value={searchQuery}
                            onChange={onChangeSearchTerm}
                            placeholder="Search by course name"
                        />
                        <select
                            className="city-select"
                            value={cityQuery}
                            onChange={onChangeCityTerm}
                        >
                            <option value="">Select City</option>
                            {cityList.map((city, index) => (
                                <option key={index} value={city}>{city}</option>
                            ))}
                        </select>
                    </div>
                </div>
                <div className="course-list">
                    {getFilteredCourses().length === 0 ? (
                        <p>No courses found matching your search.</p>
                    ) : (
                        getFilteredCourses().map((course) => {
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
                                <div
                                    className="card"
                                    key={`${course.coursesId}-${course.offeredCourseId}`}
                                    onClick={() => {
                                        navigate(`/CourseDescription/${course.offeredCourseId}`);
                                    }}
                                >
                                    <img className="img" src={courseImage} alt={courseName} />
                                    <p><b>{course.barcode}</b></p>
                                    <p><b>Course Name</b>: {courseName}</p>
                                    <p><b>No of seats </b>: {course.noOfSeats}</p>
                                    <p><b>Start Date</b>: {course.startDate}</p>
                                    <p><b>End Date </b>: {course.endDate}</p>
                                    <p>{course.courseDTO.ageGroups.description}</p>
                                    <p><b> Price :</b>${course.offeredCourseFeeDTO.courseFee}</p>
                                    <p><b>Location :</b> {course.facilititesDTO.city}</p>
                                    <button
                                        className={`cta-button ${!isAvailable ? 'disabled' : ''}`}
                                        disabled={!isAvailable}
                                        onClick={(e) => {
                                            e.stopPropagation();
                                            if (isAvailable) {
                                                // bruh will DD LATER
                                            }
                                        }}
                                    >
                                        {isAvailable ? "Register Now" : "Not Available"}
                                    </button>
                                </div>
                            );
                        })
                    )}
                </div>
            </div>
        </div>
    );
};

export default Allcourses;
