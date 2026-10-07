import {
  File,
  Image,
  Video,
  Music,
  FileText,
  FileIcon,
  Globe,
  Lock,
  Eye,
} from "lucide-react";
import { useNavigate } from "react-router-dom";

const RecentFiles = ({ files }) => {
  const navigate = useNavigate();

  const getFileIcon = (file) => {
    const extension = file.name
      ?.split(".")
      .pop()
      .toLowerCase();

    if (
      ["jpg", "jpeg", "png", "gif", "svg", "webp"].includes(
        extension
      )
    ) {
      return (
        <Image
          size={24}
          className="text-purple-500"
        />
      );
    }

    if (
      ["mp4", "webm", "mov", "avi", "mkv"].includes(
        extension
      )
    ) {
      return (
        <Video
          size={24}
          className="text-blue-500"
        />
      );
    }

    if (
      ["mp3", "wav", "ogg", "flac", "m4a"].includes(
        extension
      )
    ) {
      return (
        <Music
          size={24}
          className="text-green-500"
        />
      );
    }

    if (
      ["pdf", "doc", "docx", "txt", "rtf"].includes(
        extension
      )
    ) {
      return (
        <FileText
          size={24}
          className="text-amber-500"
        />
      );
    }

    return (
      <FileIcon
        size={24}
        className="text-purple-500"
      />
    );
  };

  const handleViewFile = (file) => {
    if (file.isPublic) {
      window.open(
        `/file/${file.id}`,
        "_blank",
        "noopener,noreferrer"
      );
    }
  };

  return (
    <div className="bg-white rounded-lg shadow-sm border border-gray-200">
      {/* Header */}
      <div className="flex items-center justify-between px-6 py-4 border-b border-gray-200">
        <div>
          <h2 className="text-lg font-semibold text-gray-800">
            Recent Files
          </h2>

          <p className="text-sm text-gray-500 mt-1">
            Your most recently uploaded files
          </p>
        </div>

        <button
          type="button"
          onClick={() => navigate("/my-files")}
          className="text-sm text-blue-600 hover:text-blue-700 font-medium"
        >
          View All
        </button>
      </div>

      {/* Empty State */}
      {files.length === 0 ? (
        <div className="p-10 flex flex-col items-center justify-center">
          <File
            size={48}
            className="text-gray-300 mb-3"
          />

          <h3 className="text-gray-700 font-medium">
            No files yet
          </h3>

          <p className="text-sm text-gray-500 mt-1 text-center">
            Upload your first file to see it here.
          </p>
        </div>
      ) : (
        <div className="divide-y divide-gray-100">
          {files.map((file) => (
            <div
              key={file.id}
              className="px-6 py-4 flex items-center justify-between gap-4 hover:bg-gray-50 transition-colors"
            >
              {/* File Information */}
              <div className="flex items-center gap-3 min-w-0">
                <div className="w-10 h-10 bg-gray-50 rounded-lg flex items-center justify-center flex-shrink-0">
                  {getFileIcon(file)}
                </div>

                <div className="min-w-0">
                  <p
                    className="text-sm font-medium text-gray-800 truncate"
                    title={file.name}
                  >
                    {file.name}
                  </p>

                  <div className="flex items-center gap-2 mt-1 text-xs text-gray-500">
                    <span>
                      {(file.size / 1024).toFixed(1)} KB
                    </span>

                    <span>•</span>

                    <span>
                      {new Date(
                        file.uploadedAt
                      ).toLocaleDateString()}
                    </span>
                  </div>
                </div>
              </div>

              {/* Sharing Status + Action */}
              <div className="flex items-center gap-3 flex-shrink-0">
                {file.isPublic ? (
                  <span className="hidden sm:flex items-center gap-1 text-xs text-green-600">
                    <Globe size={14} />
                    Public
                  </span>
                ) : (
                  <span className="hidden sm:flex items-center gap-1 text-xs text-gray-500">
                    <Lock size={14} />
                    Private
                  </span>
                )}

                {file.isPublic && (
                  <button
                    type="button"
                    onClick={() => handleViewFile(file)}
                    className="p-2 text-gray-500 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition-colors"
                    title="View file"
                  >
                    <Eye size={18} />
                  </button>
                )}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default RecentFiles;
