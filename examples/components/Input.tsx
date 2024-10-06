type InputProps = {
    value: string,
    handleChange: (event: React.ChangeEvent<HTMLInputElement>) => void,
}

export default function Input(props: InputProps) {
    //forma alterna de hacerlo
    // const inputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    //     console.log(event)
    // }

    return (
        <input type="text" value={props.value} onChange={props.handleChange}/>
    )
}
