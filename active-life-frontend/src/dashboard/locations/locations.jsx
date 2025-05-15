import {useEffect} from "react";
import {setCourses} from "../../redux/OfferedCourcesSlice.js";

const locations =() =>{

    useEffect(() => {
        const fetchCourse = async () => {
            try {
                const res = await fetch("http://localhost:40015/api/offeredcourse", {
                    method: "GET",

                })

                if (res.ok) {
                    const responseData = await res.json();

                }
                else {
                    setError("error while loading");
                }
            } catch (error) {
                console.error("Error:", error);
                setError("Invalid credentials. Please try again.");
            }
        }


    }, [ ]);


}