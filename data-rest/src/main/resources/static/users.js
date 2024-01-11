/*
 * Copyright (c) 2023-2024 joanribalta@elmolidelanoguera.com
 * License: CC BY-NC-ND 4.0 (https://creativecommons.org/licenses/by-nc-nd/4.0/)
 * Blog Consolidando: https://diy.elmolidelanoguera.com/
 */

const _USERS_GOOGLE_CLIENT_ID = '555980883878-490o9l11t3p4m32sg7oo3rg9tvu81lm6.apps.googleusercontent.com';
const _USERS_RESOURCE = "apis/users";

let _usersToken = null;
let _usersDecodeTokenPayload = null;


function usersLoadScript(fileUrl, async = true, type = "text/javascript")
{
    return new Promise((resolve, reject) => {
        try {
            const scriptElement = document.createElement("script");
            scriptElement.type = type;
            scriptElement.async = async;
            scriptElement.src = fileUrl;

            scriptElement.addEventListener("load", () => resolve());
            scriptElement.addEventListener("error", () => reject());

            document.body.appendChild(scriptElement);
        } catch (error) {
            reject(error);
        }
    });
}


function usersLoadGoogleIdentityAndRenderSignInButton(buttonId)
{
    return new Promise((resolve, reject) =>
    {
        usersLoadScript("https://accounts.google.com/gsi/client")
                .then(() => _onUsersLoadGoogleIdentityInit(buttonId))
                .then(data => {
                    _usersGetTokenAndPayload(data);
                    resolve("Google Identity loaded and sign-in callback executed successfully");
                })
                .catch(err => {
                    console.error(err);
                    reject("Error loading Google Identity");
                });
    });
}


function _onUsersLoadGoogleIdentityInit(buttonId)
{
    return new Promise((resolve, reject) => {
        try {
            google.accounts.id.initialize({
                client_id: _USERS_GOOGLE_CLIENT_ID,
                callback: data => resolve(data)
            });

            google.accounts.id.renderButton(
                    document.getElementById(buttonId),
                    {theme: "outline", size: "medium"}
            );

            google.accounts.id.prompt();


        } catch (error) {
            console.error(error);
            reject("Error initializing Google Identity");
        }
    });
}

function _usersGetTokenAndPayload(response)
{
    _usersToken = response.credential;
    //
    const base64Payload = response.credential.split('.')[1];
    _usersDecodeTokenPayload = JSON.parse(
            decodeURIComponent(escape(atob(base64Payload))));

    // 
    console.log("Id Token: " + _usersToken);
    console.log("email: " + usersGetEmail());
}

function usersGetToken()
{
    return(_usersToken);
}

function usersGetEmail()
{
    return(_usersDecodeTokenPayload.email);
}

function usersGetName()
{
    return(_usersDecodeTokenPayload.name);
}

function usersGetId(email) {
    return new Promise((resolve, reject) =>
    {
        fetch(_USERS_RESOURCE + "/id",
                {
                    method: 'GET',
                    headers:
                            {
                                'Content-Type': 'application/json',
                                Authorization: `Bearer ${_usersToken}`
                            }
                })
                .then(response =>
                {
                    if (!response.ok)
                    {
                        return response.json().then(errorBody =>
                        {
                            throw {
                                status: response.status,
                                statusText: response.statusText,
                                body: errorBody};
                        });
                    }
                    return response.json();
                })
                .then(data => resolve(data.id))
                .catch(error => reject(error));
    });
}

function usersGet() {
    return new Promise((resolve, reject) =>
    {
        fetch(_USERS_RESOURCE,
                {
                    method: 'GET',
                    headers:
                            {
                                'Content-Type': 'application/json',
                                //                            Authorization: `Bearer ${_usersToken}`
                            }
                })
                .then(response =>
                {
                    if (!response.ok)
                    {
                        return response.json().then(errorBody =>
                        {
                            throw {
                                status: response.status,
                                statusText: response.statusText,
                                body: errorBody};
                        });
                    }
                    return response.json();
                })
                .then(data => resolve(data))
                .catch(error => reject(error));
    });
}

function usersCreate(id, user)
{
    return new Promise((resolve, reject) =>
    {
        fetch(_USERS_RESOURCE + "/" + id,
                {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json',
                        Authorization: `Bearer ${_usersToken}`
                    },
                    body: JSON.stringify(user)
                })
                .then(response =>
                {
                    if (!response.ok)
                    {
                        return response.json().then(errorBody =>
                        {
                            throw {
                                status: response.status,
                                statusText: response.statusText,
                                body: errorBody};
                        });
                    }

                    return response.json();
                })
                .then(data => resolve(data))
                .catch(error => reject(error));
    });
}

function usersWarmupRequest()
{
    fetch("/warmup");
}

function usersGenerateHTMLFromObject(obj, indent = 0)
{
    var html = '';

    // Iterate over the properties of the object
    for (var key in obj) {
        if (obj.hasOwnProperty(key)) {
            // Add indentation for nested objects
            var indentation = '&nbsp;&nbsp;'.repeat(indent);

            // Check if the property is an object and call the function recursively
            if (typeof obj[key] === 'object' && obj[key] !== null) {
                html += `<div>${indentation}<strong>${key}:</strong></div>`;
                html += usersGenerateHTMLFromObject(obj[key], indent + 1);
            } else {
                html += `<div>${indentation}<strong>${key}:</strong> <span style="overflow-wrap: break-word;">${obj[key]}</span></div>`;
            }
        }
    }

    return html;
}



