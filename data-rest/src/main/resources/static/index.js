/*
 * Copyright (c) 2023 joanribalta@elmolidelanoguera.com
 * License: CC BY-NC-ND 4.0 (https://creativecommons.org/licenses/by-nc-nd/4.0/)
 * Blog Consolidando: https://diy.elmolidelanoguera.com/
 */

let _indexcCurrentUserId;

function indexStartUp()
{
    usersWarmupRequest();
    usersLoadGoogleIdentityAndRenderSignInButton("signInButton")
            .then(() =>
            {
                document.getElementById("notauthenticatedyet").style.display = "none";
                document.getElementById("token").textContent = usersGetToken();
                document.getElementById("owner").textContent = usersGetName();
                document.getElementById("email").textContent = usersGetEmail();
                document.getElementById("authenticated").style.display = "block";

                return(usersGetId(usersGetEmail()));
            })
            .then((id) =>
            {
                let getusers = document.getElementById("getusers");
                getusers.onclick = _onIndexGetUsers;
                getusers.disabled = false;
                //
                let createuser = document.getElementById("createuser");
                createuser.onclick = _onIndexCreateUser;
                createuser.disabled = false;
                //
                _indexcCurrentUserId = id;
            });
}

function _onIndexCreateUser()
{
    document.getElementById("result2").style.display = "block";
    document.getElementById("createuser").disabled = true;
    const user =
            {
                email: usersGetEmail(),
                name: 'tets',
                familyName: 'test_family'
            };
    usersCreate(_indexcCurrentUserId, user)
            .then((data) =>
            {
                document.getElementById("createuserresult").innerHTML
                        = usersGenerateHTMLFromObject(data);
            })
            .catch(error =>
            {
                document.getElementById("createuserresult").innerHTML
                        = usersGenerateHTMLFromObject(error);
            })
            .finally(() => {
                document.getElementById("createuser").disabled = false;
            });
}

function _onIndexGetUsers()
{
    document.getElementById("result1").style.display = "block";
    document.getElementById("getusers").disabled = true;

    usersGet()
            .then((data) =>
            {
                document.getElementById("getusersresult").innerHTML
                        = usersGenerateHTMLFromObject(data);
            })
            .catch(error =>
            {
                document.getElementById("getusersresult").innerHTML
                        = usersGenerateHTMLFromObject(error);
            })
            .finally(() => {
                document.getElementById("getusers").disabled = false;
            });
}


