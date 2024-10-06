type RandomNumberType = {
    value: number
}

type PositiveNumber = RandomNumberType & {
    isPositive: boolean
    isNegative?: never
    isZero?: never
}

type NegativeNumber = RandomNumberType & {
    isNegative: boolean
    isPositive?: never
    isZero?: never
}

type Zero = RandomNumberType & {
    isZero: boolean
    isNegative?: never
    isPositive?: never
}

type RandomNumberProps = PositiveNumber | NegativeNumber | Zero

export default function RandomNumber(props: RandomNumberProps) {
    return (
        <div>
            {props.value} {props.isPositive && 'positive'} {props.isNegative && 'negative'} {' '}
            {props.isZero && 'zero'}
        </div>
    )
}
