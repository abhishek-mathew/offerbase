import type { JobApplication } from "../types/application";
import Badge from "./ui/Badge";

import {
    DragDropProvider,
    useDraggable,
    useDroppable,
} from "@dnd-kit/react";


type BoardViewProps = {
    applications: JobApplication[];

    onStatusChange: (
        application: JobApplication,
        newStatus: string
    ) => Promise<void>;
};


const columns = [
    { key: "SAVED", label: "Saved" },
    { key: "APPLIED", label: "Applied" },
    { key: "INTERVIEW", label: "Interview" },
    { key: "OFFER", label: "Offer" },
    { key: "REJECTED", label: "Rejected" },
    { key: "WITHDRAWN", label: "Withdrawn" },
];


function DraggableApplicationCard({
                                      application,
                                  }: {
    application: JobApplication;
}) {

    const {
        ref,
        isDragging,
    } = useDraggable({
        id: application.id,
    });


    return (
        <article
            ref={ref}
            className={`cursor-grab rounded-xl border border-slate-200 bg-white p-4 shadow-sm transition active:cursor-grabbing ${
                isDragging
                    ? "opacity-50 shadow-lg"
                    : "hover:shadow-md"
            }`}
        >
            <h3 className="font-bold text-slate-900">
                {application.company}
            </h3>

            <p className="mt-1 text-sm text-slate-500">
                {application.position}
            </p>

            {application.location && (
                <p className="mt-3 text-xs text-slate-400">
                    {application.location}
                </p>
            )}

            <div className="mt-4">
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
        </article>
    );
}


function BoardColumn({
                         column,
                         applications,
                     }: {
    column: {
        key: string;
        label: string;
    };

    applications: JobApplication[];
}) {

    const {
        ref,
        isDropTarget,
    } = useDroppable({
        id: column.key,
    });


    return (
        <div
            ref={ref}
            className={`min-h-[280px] rounded-2xl p-3 transition ${
                isDropTarget
                    ? "bg-indigo-100 ring-2 ring-indigo-300"
                    : "bg-slate-100"
            }`}
        >
            <div className="mb-3 flex items-center justify-between px-1">
                <h2 className="text-sm font-bold text-slate-700">
                    {column.label}
                </h2>

                <span className="text-xs font-semibold text-slate-400">
                    {applications.length}
                </span>
            </div>

            <div className="grid gap-3">
                {applications.map((application) => (
                    <DraggableApplicationCard
                        key={application.id}
                        application={application}
                    />
                ))}
            </div>
        </div>
    );
}


function BoardView({
                       applications,
                       onStatusChange,
                   }: BoardViewProps) {

    async function handleDragEnd(event: any) {

        if (event.canceled) {
            return;
        }

        const source = event.operation.source;
        const target = event.operation.target;

        if (!source || !target) {
            return;
        }

        const applicationId =
            String(source.id);

        const newStatus =
            String(target.id);


        const validStatus = columns.some(
            (column) =>
                column.key === newStatus
        );

        if (!validStatus) {
            return;
        }


        const application =
            applications.find(
                (item) =>
                    item.id === applicationId
            );

        if (!application) {
            return;
        }


        if (application.status === newStatus) {
            return;
        }


        await onStatusChange(
            application,
            newStatus
        );
    }


    return (
        <DragDropProvider
            onDragEnd={handleDragEnd}
        >
            <section className="mx-auto max-w-6xl overflow-x-auto pb-4">

                <div className="grid min-w-[1200px] grid-cols-6 gap-4">

                    {columns.map((column) => {

                        const columnApplications =
                            applications.filter(
                                (application) =>
                                    application.status ===
                                    column.key
                            );

                        return (
                            <BoardColumn
                                key={column.key}
                                column={column}
                                applications={
                                    columnApplications
                                }
                            />
                        );
                    })}

                </div>

            </section>
        </DragDropProvider>
    );
}


export default BoardView;