import React from "react";
import {
    Navbar,
    MobileNav,
    Typography,
    Button,
    IconButton,
    Card,
    Avatar,
    Menu,
    MenuHandler,
    MenuList,
    MenuItem,
} from "@material-tailwind/react";
import MyIcon from "../icons/MyIcon";

type TopNavigationProps = {
    action: () => void
}

export const TopNavigation = (props: TopNavigationProps) => {
    const [isNavOpen, setIsNavOpen] = React.useState(false);

    const toggleIsNavOpen = () => setIsNavOpen((cur) => !cur);

    // React.useEffect(() => {
    //     window.addEventListener(
    //         "resize",
    //         () => window.innerWidth >= 960 && setIsNavOpen(false),
    //     );
    // }, []);

    return (
        <div className=" max-h-[768px] w-[calc(100%+48px)] p-2">
            <Navbar className="mx-auto max-w-full p-2 lg:pl-6 sticky top-0 z-10 h-max lg:px-8 lg:py-4">
                <div className="relative mx-auto flex items-center justify-between text-blue-gray-900">
                    <MyIcon name="view_headline" action={props.action} />
                    <ProfileMenu />
                </div>
            </Navbar>
        </div>
    );
}

function ProfileMenu() {
    const [isMenuOpen, setIsMenuOpen] = React.useState(false);

    const closeMenu = () => setIsMenuOpen(false);

    return (
        <Menu open={isMenuOpen} handler={setIsMenuOpen} placement="bottom-end">
            <MenuHandler>
                <Button
                    variant="text"
                    color="blue-gray"
                    className="flex items-center gap-1 rounded-full py-0.5 pr-2 pl-0.5 lg:ml-auto"
                >
                    <Avatar
                        variant="circular"
                        size="md"
                        alt=""
                        className="border border-gray-900 p-0.5"
                        src="src\assets\blank_avatar.PNG"
                    />
                </Button>
            </MenuHandler>
        </Menu>
    );
}