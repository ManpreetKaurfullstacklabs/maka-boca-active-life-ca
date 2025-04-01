import React, { useState } from "react";
import "./Login.css";
import { useNavigate } from "react-router-dom";

const Login = () => {
    const navigate = useNavigate();

    const blankedState = {
        memberLoginId: "",
        pin: "",
    };
    const [formData, setFormData] = useState(blankedState);
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);

    const handleChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value,
        });
    };


    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError("");


        try {
            const res = await fetch("http://localhost:40015/api/familyregistration/login", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(formData),
            });

            const responseData = await res.json();

            if (res.ok) {
                console.log(responseData)
                navigate('/otp', { state: { responseData } });
                console.log(responseData)
            } else {
                setError(responseData.message || "Invalid credentials. Please try again.");
            }
        } catch (error) {
            console.error("Error:", error);
            setError("Something went wrong. Please try again.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="login-container">
            <div className="login-box">
                <h2>Login</h2>
                {error && <p className="error-message">{error}</p>}
                <form onSubmit={handleSubmit}>
                    <input
                        type="text"
                        name="memberLoginId"
                        className="input-field"
                        placeholder="Username"
                        onChange={handleChange}
                        value={formData.memberLoginId}
                        required
                    />
                    <input
                        type="password"
                        name="pin"
                        className="input-field"
                        placeholder="Password"
                        onChange={handleChange}
                        value={formData.pin}
                        required
                    />
                    <h1></h1>
                    <button type="submit" className="login-btn" disabled={loading}>
                        {loading ? "Logging in..." : "Login"}
                    </button>
                </form>
            </div>
        </div>
    );
};

export default Login;
