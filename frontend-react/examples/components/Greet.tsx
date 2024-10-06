// rfce
type GreetProps = {
    name: string
    count?: number
    isLogged: boolean
    optional?: string
};

function Greet(props: GreetProps) {
    const { count = -1 } = props
    return (
        <div>
            {
                props.isLogged ? <h2>Hello, {props.name}: You have {count} unread messages</h2> : <h2>Please, Log In</h2>
            }
        </div>
    );
}

export default Greet;
