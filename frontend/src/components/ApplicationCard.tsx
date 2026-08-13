import type { JobApplication } from "../types/application";
import Button from "./ui/Button";
import Badge from "./ui/Badge";
import { formatDate } from "../utils/date";

type ApplicationCardProps = {
    application: JobApplication;
    onEdit: (application: JobApplication) => void;
    onDelete: (application: JobApplication) => void;
    onOpen: (application: JobApplication) => void;
};


function ApplicationCard({
                             application,
                             onEdit,
                             onDelete,
                             onOpen,
                         }: ApplicationCardProps) {
    return (
        <article
            onClick={() => onOpen(application)}
            className="cursor-pointer rounded-2xl border border-slate-200 bg-white p-6 shadow-sm transition hover:-translate-y-0.5 hover:shadow-md"
        >
            <div className="flex flex-col gap-4 sm:flex-row sm:items-start sm:justify-between">
                <div>
                    <h2 className="text-xl font-bold text-slate-900">
                        {application.company}
                    </h2>

                    <p className="mt-1 text-base text-slate-600">
                        {application.position}
                    </p>
                </div>

                <Badge
                    variant={
                        application.status.toLowerCase() as
                            | "saved"
                            | "applied"
                            | "interview"
                            | "offer"
                            | "rejected"
                            | "withdrawn"
                    }
                >
                    {application.status}
                </Badge>
            </div>

            {(application.location || application.dateApplied) && (
                <div className="mt-5 flex flex-wrap gap-x-6 gap-y-2 text-sm text-slate-500">
                    {application.location && (
                        <span>{application.location}</span>
                    )}

                    {application.dateApplied && (
                        <span>Applied {formatDate(application.dateApplied)}</span>
                    )}
                </div>
            )}

            {application.notes && (
                <p className="mt-4 rounded-xl bg-slate-50 p-4 text-sm text-slate-600">
                    {application.notes}
                </p>
            )}

            <div className="mt-6 flex gap-3">
                <Button
                    variant="secondary"
                    onClick={(event) => {
                        event.stopPropagation();
                        onEdit(application);
                    }}
                >
                    Edit
                </Button>

                <Button
                    variant="danger"
                    onClick={(event) => {
                        event.stopPropagation();
                        onDelete(application);
                    }}
                >
                    Delete
                </Button>
            </div>
        </article>
    );
}

export default ApplicationCard;