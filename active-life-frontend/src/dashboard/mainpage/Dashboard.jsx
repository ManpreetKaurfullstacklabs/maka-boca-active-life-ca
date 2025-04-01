import React from "react";
import { useNavigate } from "react-router-dom";
import "./Dashboard.css";

const Dashboard = () => {

    return (
        <div className="dashboard-container">
            <div className="background-video">
                <video autoPlay loop muted className="video-background">
                    <source src="/4920813-hd_1920_1080_25fps.mp4" type="video/mp4" />
                </video>
            </div>

            <div className="content-overlay">
                <h1>Welcome to Active Life</h1>
                <p>Sweat, smile, and repeat!</p>
                <button className="cta-button">Get Started</button>
            </div>
        </div>
    );
};

export default Dashboard;
