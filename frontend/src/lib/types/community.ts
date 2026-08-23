export type CommunityReply = {
  id: string;
  authorName: string;
  authorAvatar?: string;
  content: string;
  isSolution: boolean;
  likesCount: number;
  createdAt: string;
};

export type CommunityPostSummary = {
  id: string;
  authorName: string;
  authorAvatar?: string;
  title: string;
  contentSnippet: string;
  category: string;
  deviceModel?: string;
  likesCount: number;
  repliesCount: number;
  isSolved: boolean;
  createdAt: string;
};

export type CommunityPostDetail = {
  id: string;
  authorName: string;
  authorAvatar?: string;
  title: string;
  content: string;
  category: string;
  deviceModel?: string;
  likesCount: number;
  repliesCount: number;
  isSolved: boolean;
  createdAt: string;
  replies: CommunityReply[];
};
