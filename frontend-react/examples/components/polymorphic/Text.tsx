type TextOwnProps<E extends React.ElementType> = {
    size?: 'sm' | 'md' | 'lg'
    color?: 'primary' | 'secondary'
    children: React.ReactNode
    as?: E
}

type TextProps<E extends React.ElementType> = TextOwnProps<E> & Omit<React.ComponentProps<E>, keyof TextOwnProps<E>>

const Text = <E extends React.ElementType = 'div'>(props: TextProps<E>) => {
    const Component = props.as || 'div'

    return (
        <Component className={`class-with-${props.size}-${props.color}`}>
            {props.children}
        </Component>
    )
}

export default Text
