type ButtonProps = {
    caption?: string
    handleClick: () => void
    handleClick2?: (event: React.MouseEvent<HTMLButtonElement>, id: number) => void
}

function Button(props: ButtonProps) {
    // {caption, handleClick} : Button para destructurar
    const { caption = 'Event Hander Test' } = props

    return <button onClick={(event) => props.handleClick2 ? props.handleClick2(event, 1) : props.handleClick}>{caption} </button>
    // return <button onClick={(event) => props.handleClick2(event, 1)}>{caption} </button>
}

export default Button
