import React, { useEffect, useState } from 'react'
import Moment from 'moment'
import FileBrowser, { Icons } from 'react-keyed-file-browser'
import 'font-awesome/css/font-awesome.min.css'
import { CssBaseline } from '@mui/material'
import 'react-keyed-file-browser/dist/react-keyed-file-browser.css'
import ScriptEditor from './editor'

const defaultFiles = [
    {
        key: 'photos/animals/cat in a hat.png',
        modified: +Moment().subtract(1, 'hours'),
        size: 1.5 * 1024 * 1024,
    },
    {
        key: 'photos/animals/kitten_ball.png',
        modified: +Moment().subtract(3, 'days'),
        size: 545 * 1024,
    },
    {
        key: 'photos/animals/elephants.png',
        modified: +Moment().subtract(3, 'days'),
        size: 52 * 1024,
    },
    {
        key: 'photos/funny fall.gif',
        modified: +Moment().subtract(2, 'months'),
        size: 13.2 * 1024 * 1024,
    },
    {
        key: 'photos/holiday.jpg',
        modified: +Moment().subtract(25, 'days'),
        size: 85 * 1024,
    },
    {
        key: 'documents/letter chunks.doc',
        modified: +Moment().subtract(15, 'days'),
        size: 480 * 1024,
        content: "<h1> hello world!! </h1>"
    },
    {
        key: 'documents/export.pdf',
        modified: +Moment().subtract(15, 'days'),
        size: 4.2 * 1024 * 1024,
    },
];

export default function Scripts() {
    const [files, setFiles] = useState(defaultFiles);
    const [editorOpen, setEditorOpen] = useState(false);
    const [openFileName, setOpenFileName] = useState(null);
    const [openFileContent, setOpenFileContent] = useState(null);

    const handleCreateFolder = (key) => {
        /* TODO: Create at the server */
        setFiles((prevFiles) => prevFiles.concat([{
            key: key,
        }]));
    };

    const handleCreateFiles = (nfiles, prefix) => {
        /* TODO: Create at the server */
        setFiles((prevFiles) => {
            const newFiles = nfiles.map((file) => {
                let newKey = prefix
                if (prefix !== '' && prefix.substring(prefix.length - 1, prefix.length) !== '/') {
                    newKey += '/'
                }
                newKey += file.name
                return {
                    key: newKey,
                    size: file.size,
                    modified: +Moment(),
                }
            })

            const uniqueNewFiles = []
            newFiles.map((newFile) => {
                let exists = false
                prevFiles.map((existingFile) => {
                    if (existingFile.key === newFile.key) {
                        exists = true
                    }
                })
                if (!exists) {
                    uniqueNewFiles.push(newFile)
                }
            })
            return prevFiles.concat(uniqueNewFiles)
        })
    }

    const handleRenameFolder = (oldKey, newKey) => {
        /* TODO: Rename from the server */
        setFiles((prevFiles) => {
            const newFiles = []
            prevFiles.map((file) => {
                if (file.key.substr(0, oldKey.length) === oldKey) {
                    newFiles.push({
                        ...file,
                        key: file.key.replace(oldKey, newKey),
                        modified: +Moment(),
                    })
                } else {
                    newFiles.push(file)
                }
            })
            return newFiles;
        })
    };

    const handleRenameFile = (oldKey, newKey) => {
        /* TODO: Rename from the server */
        setFiles((prevFiles) => {
            const newFiles = []
            prevFiles.map((file) => {
                if (file.key === oldKey) {
                    newFiles.push({
                        ...file,
                        key: newKey,
                        modified: +Moment(),
                    })
                } else {
                    newFiles.push(file)
                }
            })
            return newFiles;
        });
    };

    const handleDeleteFolder = (folderKey) => {
        /* TODO: Delete from the server */
        setFiles((prevFiles) => {
            const newFiles = []
            prevFiles.map((file) => {
                if (file.key.substr(0, folderKey[0].length) !== folderKey[0]) {
                    newFiles.push(file)
                }
            })
            return newFiles;
        });
    }

    const handleDeleteFile = (fileKey) => {
        /* TODO: Delete from the server */
        setFiles((prevFiles) => {
            const newFiles = []
            prevFiles.map((file) => {
                if (file.key !== fileKey[0]) {
                    newFiles.push(file)
                }
            })
            return newFiles;
        })
    }

    const handleSelectFile = (file) => {
        setOpenFileName(file.key);
        /* TODO: Read file content from server */
        setOpenFileContent(file.content);
        setEditorOpen(true);
    }

    const handleSaveFile = (fileName, content) => {
        /* TODO: Save the content to the server */
        setEditorOpen(false);
        setOpenFileName(null);
        setOpenFileContent(null);
    }

    useEffect(() => {
        /* TODO: set files */
    }, []);

    if (editorOpen) {
        return <ScriptEditor fileName={openFileName} content={openFileContent} saveFile={handleSaveFile} />
    } else {
        return (
            <React.Fragment>
                <CssBaseline />
                <FileBrowser
                    files={files}
                    icons={Icons.FontAwesome(4)}

                    onCreateFolder={handleCreateFolder}
                    onCreateFiles={handleCreateFiles}
                    onMoveFolder={handleRenameFolder}
                    onMoveFile={handleRenameFile}
                    onRenameFolder={handleRenameFolder}
                    onRenameFile={handleRenameFile}
                    onDeleteFolder={handleDeleteFolder}
                    onDeleteFile={handleDeleteFile}
                    onSelectFile={handleSelectFile}
                />
            </React.Fragment>
        );
    }
}