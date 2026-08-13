import type { ChangeEvent } from "react";

type InputProps = {
    label?: string;
    type?: "text" | "email" | "password" | "search";
    value: string;
    placeholder?: string;
    required?: boolean;
    onChange: (value: string) => void;
};

function Input({
                   label,
                   type = "text",
                   value,
                   placeholder,
                   required = false,
                   onChange,
               }: InputProps) {
    function handleChange(event: ChangeEvent<HTMLInputElement>) {
        onChange(event.target.value);
    }

    const input = (
        <input
            className="w-full rounded-xl border border-slate-300 bg-white px-4 py-3 text-slate-900 outline-none transition placeholder:text-slate-400 focus:border-indigo-500 focus:ring-4 focus:ring-indigo-500/10"
            type={type}
            value={value}
            placeholder={placeholder}
            required={required}
            onChange={handleChange}
        />
    );

    if (!label) {
        return input;
    }

    return (
        <label className="grid gap-2 text-sm font-semibold text-slate-700">
            {label}
            {input}
        </label>
    );
}

export default Input;