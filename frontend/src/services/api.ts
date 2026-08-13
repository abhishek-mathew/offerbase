import type { JobApplication } from "../types/application";
import type { EmailSuggestion } from "../types/emailSuggestion";
import type { ApplicationEvent } from "../types/applicationEvent";

const API_URL = "http://localhost:8080/api";

type LoginResponse = {
    token: string;
};

type ApplicationPayload = {
    company: string;
    position: string;
    location: string;
    jobUrl: string | null;
    status: string;
    dateApplied: string | null;
    notes: string | null;
};

export async function login(
    email: string,
    password: string
): Promise<LoginResponse> {

    const response = await fetch(
        `${API_URL}/auth/login`,
        {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                email,
                password,
            }),
        }
    );

    if (!response.ok) {
        throw new Error(
            "Invalid email or password"
        );
    }

    return response.json();
}

export async function getApplications(
    token: string
): Promise<JobApplication[]> {

    const response = await fetch(
        `${API_URL}/applications`,
        {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        }
    );

    if (!response.ok) {
        throw new Error(
            "Could not load applications"
        );
    }

    return response.json();
}

export async function saveApplication(
    token: string,
    payload: ApplicationPayload,
    applicationId?: string
): Promise<JobApplication> {

    const isEditing =
        applicationId !== undefined;

    const url = isEditing
        ? `${API_URL}/applications/${applicationId}`
        : `${API_URL}/applications`;

    const response = await fetch(
        url,
        {
            method:
                isEditing
                    ? "PUT"
                    : "POST",

            headers: {
                "Content-Type":
                    "application/json",

                Authorization:
                    `Bearer ${token}`,
            },

            body: JSON.stringify(
                payload
            ),
        }
    );

    if (!response.ok) {
        throw new Error(
            isEditing
                ? "Could not update application"
                : "Could not create application"
        );
    }

    return response.json();
}

export async function deleteApplication(
    token: string,
    applicationId: string
): Promise<void> {

    const response = await fetch(
        `${API_URL}/applications/${applicationId}`,
        {
            method: "DELETE",
            headers: {
                Authorization:
                    `Bearer ${token}`,
            },
        }
    );

    if (!response.ok) {
        throw new Error(
            "Could not delete application"
        );
    }
}

export async function getEmailSuggestions(
    token: string
): Promise<EmailSuggestion[]> {

    const response = await fetch(
        `${API_URL}/gmail/emails`,
        {
            headers: {
                Authorization:
                    `Bearer ${token}`,
            },
        }
    );

    if (!response.ok) {
        throw new Error(
            "Could not load Gmail suggestions"
        );
    }

    return response.json();
}

export async function markEmailProcessed(
    token: string,
    messageId: string,
    action: "APPROVED" | "IGNORED"
): Promise<void> {

    const response = await fetch(
        `${API_URL}/gmail/emails/${messageId}/processed?action=${action}`,
        {
            method: "POST",
            headers: {
                Authorization:
                    `Bearer ${token}`,
            },
        }
    );

    if (!response.ok) {
        throw new Error(
            "Could not mark Gmail message as processed"
        );
    }
}

export async function getApplicationEvents(
    token: string,
    applicationId: string
): Promise<ApplicationEvent[]> {

    const response = await fetch(
        `${API_URL}/applications/${applicationId}/events`,
        {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        }
    );

    if (!response.ok) {
        throw new Error(
            "Could not load application history"
        );
    }

    return response.json();
}

export async function saveEmailFeedback(
    token: string,
    feedback: {
        gmailMessageId: string;
        subject: string;
        sender: string;
        body: string;
        predictedLabel: string;
        actualLabel: string;
        confidence: number;
    }
): Promise<void> {

    const response = await fetch(
        `${API_URL}/gmail/emails/feedback`,
        {
            method: "POST",
            headers: {
                "Content-Type":
                    "application/json",

                Authorization:
                    `Bearer ${token}`,
            },

            body: JSON.stringify(
                feedback
            ),
        }
    );

    if (!response.ok) {
        throw new Error(
            "Could not save email feedback"
        );
    }
}