import type { MouseEventHandler } from "react";

type ButtonProps = {
    children: React.ReactNode;
    onClick?: MouseEventHandler<HTMLButtonElement>;
    type?: "button" | "submit";
    variant?: "primary" | "secondary" | "danger";
};

function Button({
                    children,
                    onClick,
                    type = "button",
                    variant = "primary",
                }: ButtonProps) {
    const styles = {
        primary:
            "rounded-xl bg-indigo-600 px-5 py-3 font-semibold text-white transition hover:bg-indigo-500",

        secondary:
            "rounded-xl border border-slate-300 bg-white px-5 py-3 font-semibold text-slate-700 transition hover:bg-slate-100",

        danger:
            "rounded-xl bg-red-600 px-5 py-3 font-semibold text-white transition hover:bg-red-500",
    };

    return (
        <button
            type={type}
            onClick={onClick}
            className={styles[variant]}
        >
            {children}
        </button>
    );
}

export default Button;