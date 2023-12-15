/*
 * Copyright (c) 2023 joanribalta@elmolidelanoguera.com
 * License: CC BY-NC-ND 4.0 (https://creativecommons.org/licenses/by-nc-nd/4.0/)
 * Blog Consolidando: https://diy.elmolidelanoguera.com/
 */


const _GOOGLE_CLIENT_ID = '555980883878-490o9l11t3p4m32sg7oo3rg9tvu81lm6.apps.googleusercontent.com';
const _USER_RESOURCE = "/users";

let _token = null;
let _decode_token_payload = null;


function loadScript(fileUrl, async = true, type = "text/javascript")
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


function loadGoogleIdentityAndRenderSignInButton(buttonId)
{
    return new Promise((resolve, reject) =>
    {
        loadScript("https://accounts.google.com/gsi/client")
                .then(() => onLoadGoogleIdentityInit(buttonId))
                .then(data => {
                    _getTokenAndPayload(data);
                    resolve("Google Identity loaded and sign-in callback executed successfully");
                })
                .catch(err => {
                    console.error(err);
                    reject("Error loading Google Identity");
                });
    });
}


function onLoadGoogleIdentityInit(buttonId)
{
    return new Promise((resolve, reject) => {
        try {
            google.accounts.id.initialize({
                client_id: _GOOGLE_CLIENT_ID,
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

function _getTokenAndPayload(response)
{
    _token = response.credential;
    //
    const base64Payload = response.credential.split('.')[1];
    _decode_token_payload = JSON.parse(atob(base64Payload));

    // 
    console.log("Id Token: " + _token);
    console.log("email: " + getUserEmail());
}

function getToken()
{
    return(_token);
}

function getUserEmail()
{
    return(_decode_token_payload.email);
}

function getUserName()
{
    return(_decode_token_payload.name);
}

function checkUserAuthenticationInTheServer()
{
    return new Promise((resolve, reject) =>
    {
        fetch(_USER_RESOURCE,
                {
                    headers: {
                        Authorization: `Bearer ${_token}`
                    }
                })
                .then(response => response.json())
                .then(data => resolve(data))                
                .catch(error => reject(error));                
    });
}

function warmupRequest()
{
    fetch("/warmup");    
}

function generateHTMLFromObject(obj, indent = 0) 
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
        html += generateHTMLFromObject(obj[key], indent + 1);
      } else {
        html += `<div>${indentation}<strong>${key}:</strong> <span style="overflow-wrap: break-word;">${obj[key]}</span></div>`;
      }
    }
  }

  return html;
}



