type ListProps<T> = {
    items: T[]
    onClick: (value: T) => void
}

function List<T extends { id: number }>({ items, onClick }: ListProps<T>) {
    return (
        <div>
            <h2>List of items</h2>
            {items.map((it, index) => {
                return (
                    <ul key={it.id} onClick={() => console.log(it)}>
                        {it.id}
                        </ul>
                )
            })}
        </div>
    )
}

export default List
