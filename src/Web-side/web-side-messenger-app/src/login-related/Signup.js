import React, {useState} from 'react';
import apiClient from './newApiClient';


export default function Signup({isLoggedInState}) {
    const [formData, setFormData] = useState({
        field1: '',
        field2: ''
    });

    const handleSubmit = (e) => {
        e.preventDefault();
        alert(`Field 1: ${formData.field1}\nField 2: 
            ${formData.field2}`);
        console.log(tryToSignUp(formData.field1, formData.field2));
    };

    const handleChange = (e) => {
        const {name, value} = e.target;
        setFormData({
            ...formData,
            [name]: value
        });
    };

    return <div>
        <form onSubmit={handleSubmit}>
            <div>
                <label htmlFor="field1">Field 1:</label>
                <input
                    type="text"
                    id="field1"
                    name="field1"
                    value={formData.field1}
                    onChange={handleChange}
                />
            </div>

            <div>
                <label htmlFor="field2">Field 2:</label>
                <input
                    type="password"
                    id="field2"
                    name="field2"
                    value={formData.field2}
                    onChange={handleChange}
                />
            </div>

            <button type="submit">Submit</button>
        </form>
    </div>;
}

async function tryToSignUp(usernamePassed, passwordPassed) {
    const signUpData = {
        username: usernamePassed,
        password: passwordPassed,
    };

    const response = await apiClient.post('/sec/signup', signUpData)
        .then((response) => {
            console.log(response);
            console.log("Signup successful! Response data:", response.data);
            localStorage.setItem('jwtToken', response.data.jwtToken); // Store the JWT token in local storage
        }).catch(error => {
            console.error("Signup failed:", error);
            if (error.response) {
                console.error("Response data:", error.response.data);
                console.error("Response status:", error.response.status);
                console.error("Response headers:", error.response.headers);
            } else {
                console.error("Error message:", error.message);
            }
        });

}