var languageSelector = $('#language');
var codeSelector = $('#code');
var compileResponseSelector = $('#compile-response');

var submissionSelector = $('#submission-id');
var inputSelector = $('#stdin');
var tlSelector = $('#tl');
var mlSelector = $('#ml');
var runResponseSelector = $('#run-response');

$('#compile').click(function () {
    compileResponseSelector.text('Loading...');
    var payload = {
        language: languageSelector.val(),
        sourceCode: codeSelector.val()
    };
    $.ajax({
        url: '/compile-submissions/',
        type: 'POST',
        contentType:"application/json; charset=utf-8",
        data: JSON.stringify(payload)
    }).done(function (resp) {
        compileResponseSelector.text(prettyJSON(resp));
    }).fail(function (resp) {
        compileResponseSelector.text(prettyJSON(resp.responseText));
    })
});

$('#run').click(function () {
    runResponseSelector.text('Loading...');
    var payload = {
        submissionId: submissionSelector.val(),
        input: inputSelector.val(),
        memoryLimit: mlSelector.val(),
        executionTimeLimit: tlSelector.val()
    };
    $.ajax({
        url: '/submissions/',
        type: 'POST',
        contentType:"application/json; charset=utf-8",
        data: JSON.stringify(payload)
    }).done(function (resp) {
        runResponseSelector.text(prettyJSON(resp));
    }).fail(function (resp) {
        runResponseSelector.text(prettyJSON(resp.responseJSON));
    })
});


function prettyJSON(data) {
    return JSON.stringify(data, null, 2);
}
