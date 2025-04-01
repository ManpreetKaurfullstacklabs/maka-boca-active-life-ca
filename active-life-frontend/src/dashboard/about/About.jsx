import React from "react";
import "./About.css"; // Import the CSS for styling

const About = () => {
    return (
        <div className="about-container">
            {/* About Header */}
            <h1 className="about-header">About Active Life</h1>

            {/* About Content (Overlay) */}
            <div className="about-content">
                <p>
                    Welcome to Active Life, your ultimate destination for health, fitness, and well-being! We offer a diverse range of fitness programs, wellness activities, and expert-led courses to help you achieve your health goals. Whether you're looking to build strength, improve flexibility, lose weight, or embrace a healthier lifestyle, Active Life has something for everyone.
                </p>

                <div className="about-section">
                    <h2>Our Facilities & Services</h2>
                    <p>We provide state-of-the-art fitness centers and wellness programs across multiple locations, including:</p>
                    <ul>
                        <li>📍 Los Angeles</li>
                        <li>📍 San Francisco</li>
                        <li>📍 Miami</li>
                        <li>📍 Denver</li>
                        <li>📍 Chicago</li>
                        <li>📍 Dallas</li>
                        <li>📍 Houston</li>
                        <li>📍 Phoenix</li>
                        <li>📍 Las Vegas</li>
                    </ul>
                    <p>Our facilities include:</p>
                    <ul>
                        <li>✔ Fully Equipped Gym – Latest machines & free weights for strength training</li>
                        <li>✔ Swimming Pools – Indoor & outdoor pools for all skill levels</li>
                        <li>✔ Yoga & Meditation Studios – Guided sessions for balance & mindfulness</li>
                        <li>✔ Specialized Fitness Programs – Tailored workouts for weight loss, endurance, and strength</li>
                    </ul>
                </div>

                <div className="about-section">
                    <h2>Our Programs & Courses</h2>
                    <p>We offer the following specialized programs:</p>
                    <ul>
                        <li>🏋️ Physical Fitness Courses</li>
                        <li>Cardio Blast – High-intensity workouts to improve endurance</li>
                        <li>Strength Mastery – Build muscle with expert training plans</li>
                        <li>Hatha Yoga Basics – Learn the fundamentals of yoga for flexibility and relaxation</li>
                    </ul>
                    <ul>
                        <li>🧘 Yoga & Mindfulness</li>
                        <li>Yoga of Cardio – A fusion of yoga and cardio for total body wellness</li>
                        <li>Strength Training – Focused exercises to improve muscle power</li>
                        <li>Mindful Meditation – Stress-relief techniques for mental clarity</li>
                    </ul>
                    <ul>
                        <li>🥗 Health & Nutrition Courses</li>
                        <li>Lose It! Weight Loss – Structured plans to help shed extra pounds</li>
                        <li>Healthy Meals 101 – Learn to cook delicious & nutritious meals</li>
                        <li>Healthy Recipes & Wellness – Nutrition-focused guidance for a balanced life</li>
                    </ul>
                </div>

                <p>
                    At Active Life, we are dedicated to helping you transform your lifestyle, boost your energy, and feel your best. No matter your fitness level, our expert instructors and cutting-edge facilities are here to support you every step of the way.
                </p>

                <p>🚀 Join Active Life today and start your journey to a healthier, happier you!</p>
            </div>
        </div>
    );
};

export default About;
