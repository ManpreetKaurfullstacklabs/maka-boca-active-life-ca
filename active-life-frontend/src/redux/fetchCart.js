export const fetchCart = async (authToken) => {
    try {
        const familyMemberId = localStorage.getItem("familyMemberId");
        const res = await fetch(`http://localhost:40015/api/shoppingcart/getcourses/${familyMemberId}`, {
            headers: {
                "Authorization": `Bearer ${authToken}`
            }
        });
        if (res.ok) {
            return  await res.json();
        } else {
            console.error("Failed to fetch cart");
        }
    } catch (err) {
        console.error("Error fetching cart:", err);
    }
};
