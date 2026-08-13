import { useEffect, useState } from "react";
import type { JobApplication } from "../types/application";

import {
    getApplications,
    saveApplication,
    deleteApplication,
} from "../services/api";

export function useApplications(token: string) {
    const [applications, setApplications] =
        useState<JobApplication[]>([]);

    const [message, setMessage] = useState("");

    useEffect(() => {
        if (!token) {
            setApplications([]);
            return;
        }

        getApplications(token)
            .then((data) => {
                setApplications(data);
            })
            .catch(() => {
                setMessage("Could not load applications.");
            });
    }, [token]);

    async function save(
        payload: {
            company: string;
            position: string;
            location: string;
            jobUrl: string | null;
            status: string;
            dateApplied: string | null;
            notes: string | null;
        },
        applicationId?: string
    ) {
        const saved = await saveApplication(
            token,
            payload,
            applicationId
        );

        if (applicationId) {
            setApplications((current) =>
                current.map((application) =>
                    application.id === saved.id
                        ? saved
                        : application
                )
            );
        } else {
            setApplications((current) => [
                ...current,
                saved,
            ]);
        }

        return saved;
    }

    async function remove(
        application: JobApplication
    ) {
        await deleteApplication(
            token,
            application.id
        );

        setApplications((current) =>
            current.filter(
                (item) => item.id !== application.id
            )
        );
    }

    async function updateStatus(
        application: JobApplication,
        newStatus: string
    ) {
        const previousApplication = {
            ...application,
        };

        const updatedApplication = {
            ...application,
            status: newStatus,
        };


        // Move the card immediately in the UI
        setApplications((current) =>
            current.map((item) =>
                item.id === application.id
                    ? updatedApplication
                    : item
            )
        );


        try {

            const saved = await saveApplication(
                token,
                {
                    company: application.company,
                    position: application.position,
                    location:
                        application.location ?? "",

                    jobUrl:
                    application.jobUrl,

                    status:
                    newStatus,

                    dateApplied:
                        newStatus === "APPLIED" &&
                        !application.dateApplied
                            ? new Date()
                                .toISOString()
                                .slice(0, 10)
                            : application.dateApplied,

                    notes:
                    application.notes,
                },
                application.id
            );


            // Replace temporary version with backend response
            setApplications((current) =>
                current.map((item) =>
                    item.id === saved.id
                        ? saved
                        : item
                )
            );

        } catch (error) {

            // Something failed — move card back
            setApplications((current) =>
                current.map((item) =>
                    item.id === previousApplication.id
                        ? previousApplication
                        : item
                )
            );

            throw error;
        }
    }

    function clearApplications() {
        setApplications([]);
    }

    return {
        applications,
        message,
        setMessage,
        save,
        remove,
        updateStatus,
        clearApplications,
    };
}