// src/redux/OfferedCourseSlice.js
import { createSlice } from "@reduxjs/toolkit";

const initialState = {
    courses: [],
    error: null
};

const offeredCourseSlice = createSlice({
    name: "offeredCourses",
    initialState,
    reducers: {
        setCourses: (state, action) => {
            state.courses = action.payload;
            state.error = null;
        },
        setError: (state, action) => {
            state.error = action.payload;
        }
    }
});

export const { setCourses, setError } = offeredCourseSlice.actions;
export default offeredCourseSlice.reducer;
