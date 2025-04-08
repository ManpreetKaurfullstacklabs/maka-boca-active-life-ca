// redux/CartSlice.js
import { createSlice } from '@reduxjs/toolkit';

const loadCartFromLocalStorage = () => {
    try {
        const serializedState = localStorage.getItem('cart');
        return serializedState ? JSON.parse(serializedState) : [];
    } catch (e) {
        console.warn("Could not load cart from localStorage", e);
        return [];
    }
};

const saveCartToLocalStorage = (cartItems) => {
    try {
        const serializedState = JSON.stringify(cartItems);
        localStorage.setItem('cart', serializedState);
    } catch (e) {
        console.warn("Could not save cart to localStorage", e);
    }
};

const cartSlice = createSlice({
    name: 'cart',
    initialState: {
        items: loadCartFromLocalStorage()
    },
    reducers: {
        loadDataFromBackend: (state, action) => {
            state.items = action.payload
        },
        addToCart: (state, action) => {
            const exists = state.items.find(item => item.offeredCourseId === action.payload.offeredCourseId);
            if (!exists) {
                state.items.push(action.payload);
                saveCartToLocalStorage(state.items);
            }
        },
        removeFromCart: (state, action) => {
            state.items = state.items.filter(item => item.offeredCourseId !== action.payload.offeredCourseId);
            saveCartToLocalStorage(state.items);
        },
        clearCart: (state) => {
            state.items = [];
            saveCartToLocalStorage([]);
        },
        initializeCartFromLocalStorage: (state) => {
            state.items = loadCartFromLocalStorage();
        }

    }
});

export const { initializeCartFromLocalStorage,loadDataFromBackend, addToCart, removeFromCart, clearCart } = cartSlice.actions;
export default cartSlice.reducer;
