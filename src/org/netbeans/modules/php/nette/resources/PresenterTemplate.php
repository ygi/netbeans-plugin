<?php

/**
 * Description of ${name}
 *
 * @author ${user}
 */
class ${name} extends BasePresenter {

	/**
	 * (non-phpDoc)
	 *
	 * @see Nette\Application\Presenter#startup()
	 */
	protected function startup() {
		parent::startup();
	}
    <#list actions as action>
        <#if action.action>

        public function action${action.name?cap_first}() {

        }
        </#if>
        <#if action.render>

        public function render${action.name?cap_first}() {

        }
        </#if>
    </#list>
        
}