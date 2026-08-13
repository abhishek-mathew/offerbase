import type { FormEvent } from "react";
import Button from "./ui/Button";
import Input from "./ui/Input";

type ApplicationFormProps = {
    company: string;
    position: string;
    location: string;
    status: string;
    onCompanyChange: (value: string) => void;
    onPositionChange: (value: string) => void;
    onLocationChange: (value: string) => void;
    onStatusChange: (value: string) => void;
    onSubmit: (event: FormEvent<HTMLFormElement>) => void;
    onCancel: () => void;
    isEditing: boolean;
};

function ApplicationForm({
                             company,
                             position,
                             location,
                             status,
                             onCompanyChange,
                             onPositionChange,
                             onLocationChange,
                             onStatusChange,
                             onSubmit,
                             onCancel,
                             isEditing,
                         }: ApplicationFormProps) {
    return (
        <section className="mx-auto mb-6 max-w-6xl rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
            <div className="flex items-start justify-between gap-4">
                <div>
                    <h2 className="text-2xl font-bold text-slate-900">
                        {isEditing ? "Edit Application" : "New Application"}
                    </h2>

                    <p className="mt-1 text-sm text-slate-500">
                        {isEditing
                            ? "Update the details for this application."
                            : "Add a role to your application tracker."}
                    </p>
                </div>
            </div>

            <form
                className="mt-6 grid gap-4"
                onSubmit={onSubmit}
            >
                <div className="grid gap-4 md:grid-cols-2">
                    <Input
                        label="Company"
                        value={company}
                        placeholder="Capital One"
                        required
                        onChange={onCompanyChange}
                    />

                    <Input
                        label="Position"
                        value={position}
                        placeholder="Software Engineering Intern"
                        required
                        onChange={onPositionChange}
                    />
                </div>

                <div className="grid gap-4 md:grid-cols-2">
                    <Input
                        label="Location"
                        value={location}
                        placeholder="McLean, VA"
                        onChange={onLocationChange}
                    />

                    <label className="grid gap-2 text-sm font-semibold text-slate-700">
                        Status

                        <select
                            className="rounded-xl border border-slate-300 bg-white px-4 py-3 text-slate-900 outline-none transition focus:border-indigo-500 focus:ring-4 focus:ring-indigo-500/10"
                            value={status}
                            onChange={(event) => onStatusChange(event.target.value)}
                        >
                            <option>SAVED</option>
                            <option>APPLIED</option>
                            <option>INTERVIEW</option>
                            <option>REJECTED</option>
                            <option>OFFER</option>
                            <option>WITHDRAWN</option>
                        </select>
                    </label>
                </div>

                <div className="mt-2 flex justify-end gap-3">
                    <Button
                        variant="secondary"
                        onClick={onCancel}
                    >
                        Cancel
                    </Button>

                    <Button type="submit">
                        {isEditing ? "Save Changes" : "Save Application"}
                    </Button>
                </div>
            </form>
        </section>
    );
}

export default ApplicationForm;