import React, { useState } from "react";
import "./Signup.css";
import { useNavigate } from "react-router-dom";

const Signup = () => {

    const blankedState = {
        memberName:"",
        dob: "",
        gender: "",
        email: "",
        streetNo: "",
        streetName:"",
        preferredMode: "",
        city: "",
        province: "",
        country: "",
        postalCode: "",
        bussinessPhoneNo: "",
        homePhoneNo:"",
        language: "",
        memberLoginId: ""
    }
    const [formData, setFormData] = useState(blankedState);

    const [formSubmitted , setFormSubmitted ] = useState(false)
    const [response , setResponse ] = useState('')

    const navigate = useNavigate();




    const handleChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value,
        });
    };


    const handleSubmit = async (e) => {
        e.preventDefault();

        try {
            const response = await fetch("http://localhost:40015/api/familyregistration/signup", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(formData),
            });

            const responseData = await response.text();

            if (response.ok) {
                setFormSubmitted(true)
                setResponse(responseData)
                setFormData(blankedState)
                alert("Signup successful! Please check your phone for an activation link.")
                setTimeout(() => {
                    navigate('/login', { state: { responseData } });
                }, 2000);

            } else {
                alert("Signup failed! " + responseData);
            }
        } catch (error) {
            console.error("Error:", error);
            alert(error);

        }
    };


    return (
        <div className="signup-container">
            {!formSubmitted ? <div className="signup-box">
                <h2>Signup</h2><form onSubmit={handleSubmit}>
                <input type="text" name="memberName" className="input-field" placeholder="Member Name" onChange={handleChange} required />
                <input type="date" name="dob" className="input-field" placeholder="DOB" onChange={handleChange} required />
                <select name="gender" className="input-field" onChange={handleChange} required>
                    <option value="">Select Gender</option>
                    <option value="Male">Male</option>
                    <option value="Female">Female</option>
                    <option value="Other">Other</option>
                </select>
                <input type="email" name="email" className="input-field" placeholder="Email" onChange={handleChange} required />
                <input type="text" name="streetNo" className="input-field" placeholder="Street No" onChange={handleChange} required />
                <input type="text" name="streetName" className="input-field" placeholder="Street Name" onChange={handleChange} required />
                <input type="text" name="preferredMode" className="input-field" placeholder="Preferred Mode" onChange={handleChange} required />
                <input type="text" name="city" className="input-field" placeholder="City" onChange={handleChange} required />
                <input type="text" name="province" className="input-field" placeholder="Province" onChange={handleChange} required />
                <input type="text" name="country" className="input-field" placeholder="Country" onChange={handleChange} required />
                <input type="text" name="postalCode" className="input-field" placeholder="Postal Code" onChange={handleChange} required />
                <input type="tel" name="bussinessPhoneNo" className="input-field" placeholder="Business Phone No" onChange={handleChange} />
                <input type="tel" name="homePhoneNo" className="input-field" placeholder="Home Phone No" onChange={handleChange} />
                <input type="text" name="language" className="input-field" placeholder="Language" onChange={handleChange} required />
                <input type="text" name="memberLoginId" className="input-field" placeholder="Member ID" onChange={handleChange} required />
                <button type="submit" className="signup-btn">Sign Up</button>
            </form>
            </div> : response }
        </div>
    );
};

export default Signup;
