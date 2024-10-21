import React from 'react'

export type NavItemProps = {
    caption: string,
    icon?: string,
    suffix?: string,
    action?: () => void
}

export const NavItem = (props: NavItemProps) => {
    return (
        <nav onClick={props.action} className="flex flex-col gap-1 min-w-[240px] p-2 font-sans text-base font-normal text-blue-gray-700">
            <div role="button" className="flex items-center w-full p-3 rounded-lg text-start leading-tight transition-all hover:bg-blue-gray-50 hover:bg-opacity-80 focus:bg-blue-gray-50 focus:bg-opacity-80 active:bg-blue-gray-50 active:bg-opacity-80 hover:text-blue-gray-900 focus:text-blue-gray-900 active:text-blue-gray-900 outline-none">
                <span className="material-icons">
                    {props.icon}
                </span>
                <span>
                    {props.caption}
                </span>
                <div className="grid grid-cols-12 place-items-center mr-4">
                    <span className="material-icons col-start-12">
                        {props.suffix}
                    </span>
                </div>

            </div>
        </nav>
    )
}
