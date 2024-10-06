import React, { useReducer, } from 'react'

type CounterState = {
    count: number,
}

type UpdateAction = {
    type: 'increment' | 'decrement'
    payload: number
}

type ResetAction = {
    type: 'reset'
}

type CounterAction = UpdateAction | ResetAction

const initialState = { count: 0 }

function reducer(state: CounterState, action: CounterAction) {
    switch (action.type) {
        case 'increment':
            return { count: state.count + action.payload }
        case 'decrement':
            return { count: state.count - action.payload }
        case 'reset':
            return initialState
        default:
            return state
    }
}

export function Counter() {

    const [state, dispatch] = useReducer(reducer, initialState)

    return (
        <>
            Count: {state.count}
            <button onClick={() => dispatch({ type: 'increment', payload: 10 })}>Sum 10</button>
            <button onClick={() => dispatch({ type: 'decrement', payload: 10 })}>Subtract 10</button>
            <button onClick={() => dispatch({ type: 'reset' })}>Subtract 10</button>
        </>
    )
}
