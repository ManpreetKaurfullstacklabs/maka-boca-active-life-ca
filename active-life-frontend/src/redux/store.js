import { configureStore } from '@reduxjs/toolkit';
import cartReducer from './CartSlice.js';
import offeredCourseReducer from './OfferedCourcesSlice.js'

export const store = configureStore({
    reducer: {
        cart: cartReducer,
        offeredCourses: offeredCourseReducer,
    },
});
