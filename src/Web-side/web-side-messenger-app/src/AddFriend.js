import apiClient from "./login-related/apiClient";
import {useState} from "react";

const AddFriend = () => {
    const [formData, setFormData] = useState({
        username: ''
    });
    const handleSubmit = (e) => {
        e.preventDefault();
        alert(`Field 1: ${formData.username}`);
        console.log(tryToAddFriend(formData.username));
    };

    const handleChange = (e) => {
        const {name, value} = e.target;
        setFormData({
            ...formData,
            [name]: value
        });
    };

    return (<div>
        <label>Username to add: </label>
        <form onSubmit={handleSubmit}>
            <input
                type="text"
                id="username"
                name="username"
                value={formData.username}
                onChange={handleChange}
            />
            <button type="submit">Add friend</button>
            Add the added friend if added successfully into the sidebar with friends
            Make sidebar dynamic too? If someone adds our current user.. Won't require adding anything manually.
        </form>

    </div>);
}

async function tryToAddFriend(usernamePassed) {
    const addFriendData = {
        username: usernamePassed,
    };

    try {
        const response = await apiClient.post
        ('/friends/add-friend', addFriendData.username);
        console.log(
            "Added friend successfully! Response data: ",
            response.data
        );
        if (response.satus = 200) {
            console.log("AAAAA");
        }
    } catch (error) {
        console.log("Couldn't add friend with nickname ",
            addFriendData.username);
        console.error("Error during add friend request: ",
            error);
    }

}

export default AddFriend;