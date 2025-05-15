import { configureStore } from '@reduxjs/toolkit';
import cartReducer, {loadDataFromBackend} from './CartSlice.js';
import offeredCourseReducer from './OfferedCourcesSlice.js'
import {fetchCart} from "./fetchCart.js";


export const store = configureStore({
    reducer: {
        cart: cartReducer,
        offeredCourses: offeredCourseReducer,
    },
});

