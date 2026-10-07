import { Upload, X, FileText, Loader2 } from "lucide-react";

const UploadBox = ({
  files,
  onFileChange,
  onUpload,
  uploading,
  onRemoveFile,
  remainingCredits,
  isUploadDisabled,
}) => {
  return (
    <div className="max-w-3xl mx-auto">
      <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
        {/* Header */}
        <div className="mb-6">
          <h2 className="text-xl font-semibold text-gray-800">
            Upload Files
          </h2>

          <p className="text-sm text-gray-500 mt-1">
            Select up to 10 files to upload.
          </p>

          <p className="text-sm text-blue-600 mt-2">
            Remaining credits:{" "}
            <span className="font-semibold">{remainingCredits}</span>
          </p>
        </div>

        {/* File input */}
        <label
          htmlFor="file-upload"
          className={`flex flex-col items-center justify-center border-2 border-dashed rounded-xl p-8 transition cursor-pointer ${
            uploading
              ? "bg-gray-100 border-gray-300 cursor-not-allowed"
              : "border-blue-300 hover:border-blue-500 hover:bg-blue-50"
          }`}
        >
          <Upload size={40} className="text-blue-500 mb-3" />

          <p className="text-gray-700 font-medium">
            Click to select files
          </p>

          <p className="text-sm text-gray-500 mt-1">
            You can select up to 10 files
          </p>

          <input
            id="file-upload"
            type="file"
            multiple
            disabled={uploading}
            onChange={onFileChange}
            className="hidden"
          />
        </label>

        {/* Selected files */}
        {files.length > 0 && (
          <div className="mt-6">
            <div className="flex items-center justify-between mb-3">
              <h3 className="font-medium text-gray-700">
                Selected Files ({files.length})
              </h3>

              <span className="text-sm text-gray-500">
                {files.length}/5
              </span>
            </div>

            <div className="space-y-2">
              {files.map((file, index) => (
                <div
                  key={`${file.name}-${index}`}
                  className="flex items-center justify-between bg-gray-50 border border-gray-200 rounded-lg p-3"
                >
                  <div className="flex items-center gap-3 min-w-0">
                    <FileText
                      size={22}
                      className="text-blue-500 flex-shrink-0"
                    />

                    <div className="min-w-0">
                      <p className="text-sm font-medium text-gray-700 truncate">
                        {file.name}
                      </p>

                      <p className="text-xs text-gray-500">
                        {(file.size / 1024 / 1024).toFixed(2)} MB
                      </p>
                    </div>
                  </div>

                  <button
                    type="button"
                    onClick={() => onRemoveFile(index)}
                    disabled={uploading}
                    className="ml-3 p-1.5 rounded-full text-gray-500 hover:text-red-600 hover:bg-red-50 disabled:opacity-50"
                  >
                    <X size={18} />
                  </button>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Upload button */}
        <button
          type="button"
          onClick={onUpload}
          disabled={isUploadDisabled || uploading}
          className={`w-full mt-6 py-3 px-4 rounded-lg font-medium flex items-center justify-center gap-2 transition ${
            isUploadDisabled || uploading
              ? "bg-gray-300 text-gray-500 cursor-not-allowed"
              : "bg-blue-600 text-white hover:bg-blue-700"
          }`}
        >
          {uploading ? (
            <>
              <Loader2 size={20} className="animate-spin" />
              Uploading...
            </>
          ) : (
            <>
              <Upload size={20} />
              Upload Files
            </>
          )}
        </button>

        {/* Credit warning */}
        {files.length > remainingCredits && remainingCredits > 0 && (
          <p className="text-sm text-red-600 text-center mt-3">
            You only have {remainingCredits} credit
            {remainingCredits !== 1 ? "s" : ""} remaining.
            Please select fewer files.
          </p>
        )}

        {remainingCredits <= 0 && (
          <p className="text-sm text-red-600 text-center mt-3">
            You don't have enough credits to upload files.
          </p>
        )}
      </div>
    </div>
  );
};

export default UploadBox;
