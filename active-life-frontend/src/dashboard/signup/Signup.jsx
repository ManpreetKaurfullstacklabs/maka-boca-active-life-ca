import React, { useState } from "react";
import "./MemberSignup.css";
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
                alert("Signup successful! Please check your phone for an activation link. and your password is "+ responseData)
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
        <div className="signup-wrapper">
            {!formSubmitted ? (
                <div className="signup-card">
                    <h2>Registration Form</h2>
                    <form onSubmit={handleSubmit} className="form-grid">
                        <div className="form-section">
                            <input type="text" name="memberName" className="form-input" placeholder="Member Name" onChange={handleChange} required />
                            <input type="date" name="dob" className="form-input" placeholder="DOB" onChange={handleChange} required />
                            <select name="gender" className="form-input" onChange={handleChange} required>
                                <option value="">Select Gender</option>
                                <option value="Male">Male</option>
                                <option value="Female">Female</option>
                            </select>
                            <input type="email" name="email" className="form-input" placeholder="Email" onChange={handleChange} required />
                            <input type="text" name="streetNo" className="form-input" placeholder="Street No" onChange={handleChange} required />
                            <input type="text" name="streetName" className="form-input" placeholder="Street Name" onChange={handleChange} required />
                            <input type="text" name="preferredMode" className="form-input" placeholder="Preferred Mode" onChange={handleChange} required />
                        </div>

                        <div className="form-section">
                            <input type="text" name="city" className="form-input" placeholder="City" onChange={handleChange} required />
                            <input type="text" name="province" className="form-input" placeholder="Province" onChange={handleChange} required />
                            <div className="form-row">
                                <input type="text" name="country" className="form-input half-width" placeholder="Country" onChange={handleChange} required />
                                <input type="text" name="postalCode" className="form-input half-width" placeholder="Postal Code" onChange={handleChange} required />
                            </div>
                            <input type="tel" name="bussinessPhoneNo" className="form-input" placeholder="Business Phone No" onChange={handleChange} />
                            <input type="tel" name="homePhoneNo" className="form-input" placeholder="Home Phone No" onChange={handleChange} />
                            <input type="text" name="language" className="form-input" placeholder="Language" onChange={handleChange} required />
                            <input type="text" name="memberLoginId" className="form-input" placeholder="Member ID" onChange={handleChange} required />
                        </div>

                        <div className="form-actions">
                            <button type="submit" className="form-submit-btn">Sign Up</button>
                        </div>
                    </form>
                </div>
            ) : (
                <p>{response}</p>
            )}
        </div>

    );
};

export default Signup;
