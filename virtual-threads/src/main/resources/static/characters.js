const _CHARACTERS_RESOURCE = "apis/characters";

function characterStartUp(buttonId)
{
    var startTestElement = document.getElementById(buttonId);
    if (startTestElement)
    {
        startTestElement.onclick = charactersGetAll;
    }
}

function charactersGetAll()
{
    const startTime = Date.now();

    charactersGetIds()
            .then(ids =>
            {
                const requests = ids.map(id =>
                {
                    const requestStartTime = Date.now();
                    return charactersGet(id.id)
                            .then(character =>
                            {
                                const requestEndTime = Date.now();
                                const requestTime = requestEndTime - requestStartTime;
                                console.log(`Character ${id.id} fetched in ${requestTime} ms`);
                                return character;
                            });
                });

                return Promise.all(requests);
            })
            .then(characters =>
            {

                const totalTime = Date.now() - startTime;
                console.log(`Total time taken: ${totalTime} ms`);

                characters.forEach(character =>
                {
                    console.log("Character:", character);
                });
            })
            .catch(error =>
            {
                console.error("Error fetching characters:", error);
            });
}


function charactersGetIds()
{
    return new Promise((resolve, reject) =>
    {
        fetch(_CHARACTERS_RESOURCE + "/ids",
                {
                    method: 'GET',
                    headers:
                            {
                                'Content-Type': 'application/json'
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

function charactersGet(id)
{
    return new Promise((resolve, reject) =>
    {
        fetch(_CHARACTERS_RESOURCE + "/" + id,
                {
                    method: 'GET',
                    headers:
                            {
                                'Content-Type': 'application/json'
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