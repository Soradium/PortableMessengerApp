/* 
as for beta version of Sidebar, it will be 
represented with as a text input field for beta functionality of accessing particular 
person for chat reasons, BUT IT HAS TO BE REWORKED FROM GROUND UP!!!!!
*/

import {useState} from "react";
import apiClient from "./login-related/apiClient";

const SidebarUi = (props) => {

    const [openedInChat, setOpenedInChat] = props.currentChatTargetState;

    const [formData, setFormData] = useState({
        targetName: ''
    });

    const handleSubmit = (e) => {
        e.preventDefault();
        alert(`Field 1: ${formData.targetName}`);
        const result = (tryToGetChatTarget(setOpenedInChat, 'USER', formData.targetName));
    };

    const handleChange = (e) => {
        const {name, value} = e.target;
        setFormData({
            ...formData,
            [name]: value
        });

    };

    return (
        <div>
            <label>Choose user to chat with: </label>
            <form onSubmit={handleSubmit}>
                <input
                    type="text"
                    id="targetName"
                    name="targetName"
                    value={formData.targetName}
                    onChange={handleChange}
                />
                <button type="submit">Enter chat with</button>
            </form>
        </div>);
}

async function tryToGetChatTarget(setOpenedInChat, targetTypePassed, targetNamePassed) {
    const addTargetData = {
        target: targetNamePassed,
        targetType: targetTypePassed
    };

    try {
        console.log(addTargetData);
        const response = await apiClient.post
        ('/chat/find-target', addTargetData);
        if (response.status == 204) {
            console.log("No target found to chat with using target name: ",
                addTargetData.target, " response: ", response);
            return false;
        } else {
            console.log(
                "Found target to chat with successfully! Response data: ",
                response
            );

            if (response.satus = 200) {
                setOpenedInChat(addTargetData.target);
                return true;
            }
        }
    } catch (error) {
        console.log("Couldn't find target target to chat with using target name: ",
            addTargetData.target);
        console.error("Error during find target to chat with request: ",
            error);
        return false;
    }

}

export default SidebarUi;
