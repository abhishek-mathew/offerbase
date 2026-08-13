type BadgeProps = {
    children: React.ReactNode;
    variant:
        | "saved"
        | "applied"
        | "interview"
        | "offer"
        | "rejected"
        | "withdrawn";
};

function Badge({
                   children,
                   variant,
               }: BadgeProps) {
    const styles = {
        saved:
            "bg-slate-200 text-slate-700",

        applied:
            "bg-blue-100 text-blue-700",

        interview:
            "bg-amber-100 text-amber-700",

        offer:
            "bg-green-100 text-green-700",

        rejected:
            "bg-red-100 text-red-700",

        withdrawn:
            "bg-purple-100 text-purple-700",
    };

    return (
        <span
            className={`inline-flex rounded-full px-3 py-1 text-xs font-bold uppercase tracking-wide ${styles[variant]}`}
        >
      {children}
    </span>
    );
}

export default Badge;