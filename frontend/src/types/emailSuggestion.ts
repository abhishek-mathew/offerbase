export type EmailSuggestion = {
    id: string;
    subject: string;
    from: string;
    date: string;
    snippet: string;

    classification: string;
    confidence: number;

    company: string;
    position: string;

    matchFound: boolean;
    matchConfidence: number;

    suggestionType: "UPDATE" | "CREATE" | "REVIEW";

    matchedApplicationId?: string;
    currentStatus?: string;
    suggestedStatus?: string;
};