import { useState } from "react"

type Authuser = {
    name: string,
    email: string,
}

function User() {

    const [user, setUser] = useState<Authuser | null>(null)
    // const [user, setUser] = useState<Authuser>({} as Authuser) -> Type assertion

    const handleLogin = () => {
        setUser({ name: 'Michael', email: 'mymail@mail.com' })
    }

    const handleLogout = () => {
        setUser(null)
    }

    return (
        <div>
            <button onClick={handleLogin}>Login2</button>
            <button onClick={handleLogout}>Logout2</button>
            <h1>Current state: {user ? 'User: ' + user.name : 'Logged Out'}</h1>
            {/* <h1>Current state: { 'User: ' + user.name}</h1> */}
        </div>
    )
}

export default User
