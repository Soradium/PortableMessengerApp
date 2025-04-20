import Login from './login-related/Login';
import {useState} from 'react';
import Signup from './login-related/Signup';
import AddFriend from './AddFriend';
import SidebarUi from './SidebarUi';
import ChatMenu from './ChatMenu'; // Import ChatMenu

function App() {
    const [isLoggedIn, setLoggedIn] = useState();
    const [openedInChat, setOpenedInChat] = useState('nullTarget');

    return (
        <div className="App">
            Login form:
            <Login/>
            <br/><br/>
            Signup form:
            <Signup/>
            <br/><br/>
            Friend add form:
            <AddFriend/>
            <br/><br/>
            {/* Group add form:
      <AddGroup/> */}
            <br/><br/>
            Sidebar choice form:
            <SidebarUi currentChatTargetState={[openedInChat, setOpenedInChat]}/>
            <br/><br/>
            Opened in chat: {openedInChat}
            <br/><br/>
            {/* <WebSockets/> */}
            <br/><br/>
            Chat Menu:
            {openedInChat == 'nullTarget' ? 'No user chosen yet' : <ChatMenu targetUserName={openedInChat}/>}
            <br/><br/>
        </div>
    );
}


export default App;
